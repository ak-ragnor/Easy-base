/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service;

import com.easybase.core.auth.entity.ResourceAction;
import com.easybase.core.auth.entity.RolePermission;
import com.easybase.core.auth.repository.ResourceActionRepository;
import com.easybase.core.auth.repository.RolePermissionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for calculating and checking permissions using bitwise operations.
 * Provides efficient permission checks using bitmasks.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class PermissionCalculatorService {

	/**
	 * Add permission to a role.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param actionKey the action key
	 */
	@CacheEvict(
		allEntries = true,
		value = {"rolePermissions", "combinedRolePermissions"}
	)
	@Transactional
	public void addPermission(
		UUID roleId, String resourceType, String actionKey) {

		Optional<ResourceAction> actionOpt =
			_resourceActionRepository.findByResourceTypeAndActionKey(
				resourceType, actionKey);

		if (actionOpt.isEmpty()) {
			throw new IllegalArgumentException(
				"Action '" + actionKey + "' not found for resource type '" +
					resourceType + "'");
		}

		RolePermission rolePerm =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType
			).orElse(
				new RolePermission(roleId, resourceType)
			);

		rolePerm.addPermission(
			actionOpt.get(
			).getBitValue());
		_rolePermissionRepository.save(rolePerm);
	}

	/**
	 * Calculate permission mask from a list of action keys.
	 *
	 * @param actionKeys the action keys to combine
	 * @param resourceType the resource type
	 * @return the combined permission mask
	 */
	public long calculateMask(List<String> actionKeys, String resourceType) {
		long mask = 0L;

		for (String actionKey : actionKeys) {
			Optional<ResourceAction> actionOpt =
				_resourceActionRepository.findByResourceTypeAndActionKey(
					resourceType, actionKey);

			if (actionOpt.isPresent()) {
				mask |= actionOpt.get(
				).getBitValue();
			}
			else {
				log.warn(
					"Action '{}' not found for resource type '{}'", actionKey,
					resourceType);
			}
		}

		return mask;
	}

	/**
	 * Calculate permission mask from bit values.
	 *
	 * @param bitValues the bit values to combine
	 * @return the combined permission mask
	 */
	public long combinePermissions(int... bitValues) {
		long mask = 0L;

		for (int bitValue : bitValues) {
			mask |= bitValue;
		}

		return mask;
	}

	/**
	 * Get the combined permission mask for all roles.
	 *
	 * @param roleIds the role IDs
	 * @param resourceType the resource type
	 * @return the combined permission mask
	 */
	@Cacheable(
		key = "#roleIds + '_' + #resourceType",
		value = "combinedRolePermissions"
	)
	@Transactional(readOnly = true)
	public long getCombinedPermissionsMask(
		List<UUID> roleIds, String resourceType) {

		long combinedMask = 0L;

		for (UUID roleId : roleIds) {
			Optional<RolePermission> rolePermOpt =
				_rolePermissionRepository.findByRoleIdAndResourceType(
					roleId, resourceType);

			if (rolePermOpt.isPresent()) {
				combinedMask |= rolePermOpt.get(
				).getPermissionsMask();
			}
		}

		return combinedMask;
	}

	/**
	 * Get all action keys a role has for a specific resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @return list of action keys
	 */
	@Transactional(readOnly = true)
	public List<String> getPermittedActions(UUID roleId, String resourceType) {
		Optional<RolePermission> rolePermOpt =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType);

		if (rolePermOpt.isEmpty()) {
			return List.of();
		}

		RolePermission rolePerm = rolePermOpt.get();
		List<ResourceAction> allActions =
			_resourceActionRepository.findByResourceTypeAndActiveTrue(
				resourceType);

		return allActions.stream(
		).filter(
			action -> rolePerm.hasPermission(action.getBitValue())
		).map(
			ResourceAction::getActionKey
		).collect(
			Collectors.toList()
		);
	}

	/**
	 * Check if a mask contains a specific permission.
	 *
	 * @param mask the permission mask
	 * @param bitValue the bit value to check
	 * @return true if the permission is granted
	 */
	public boolean hasPermission(long mask, int bitValue) {
		if ((mask & bitValue) != 0) {
			return true;
		}

		return false;
	}

	/**
	 * Check if a role has a specific permission on a resource.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param actionKey the action key
	 * @return true if the role has the permission
	 */
	@Cacheable(
		key = "#roleId + '_' + #resourceType + '_' + #actionKey",
		value = "rolePermissions"
	)
	@Transactional(readOnly = true)
	public boolean hasPermission(
		UUID roleId, String resourceType, String actionKey) {

		// Get the action

		Optional<ResourceAction> actionOpt =
			_resourceActionRepository.findByResourceTypeAndActionKey(
				resourceType, actionKey);

		if (actionOpt.isEmpty()) {
			log.warn(
				"Action '{}' not found for resource type '{}'", actionKey,
				resourceType);

			return false;
		}

		// Get role permission

		Optional<RolePermission> rolePermOpt =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType);

		if (rolePermOpt.isEmpty()) {
			return false;
		}

		return rolePermOpt.get(
		).hasPermission(
			actionOpt.get(
			).getBitValue()
		);
	}

	/**
	 * Check if any of the given roles has a specific permission.
	 *
	 * @param roleIds the role IDs to check
	 * @param resourceType the resource type
	 * @param actionKey the action key
	 * @return true if any role has the permission
	 */
	@Transactional(readOnly = true)
	public boolean hasPermissionAnyRole(
		List<UUID> roleIds, String resourceType, String actionKey) {

		for (UUID roleId : roleIds) {
			if (hasPermission(roleId, resourceType, actionKey)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Remove permission from a role.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param actionKey the action key
	 */
	@CacheEvict(
		allEntries = true,
		value = {"rolePermissions", "combinedRolePermissions"}
	)
	@Transactional
	public void removePermission(
		UUID roleId, String resourceType, String actionKey) {

		Optional<ResourceAction> actionOpt =
			_resourceActionRepository.findByResourceTypeAndActionKey(
				resourceType, actionKey);

		if (actionOpt.isEmpty()) {
			return;
		}

		Optional<RolePermission> rolePermOpt =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType);

		if (rolePermOpt.isEmpty()) {
			return;
		}

		RolePermission rolePerm = rolePermOpt.get();

		rolePerm.removePermission(
			actionOpt.get(
			).getBitValue());

		if (rolePerm.getPermissionsMask() == 0) {
			_rolePermissionRepository.delete(rolePerm);
		}
		else {
			_rolePermissionRepository.save(rolePerm);
		}
	}

	/**
	 * Set exact permissions for a role on a resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param actionKeys the action keys to set
	 */
	@CacheEvict(
		allEntries = true,
		value = {"rolePermissions", "combinedRolePermissions"}
	)
	@Transactional
	public void setPermissions(
		UUID roleId, String resourceType, List<String> actionKeys) {

		long mask = calculateMask(actionKeys, resourceType);

		RolePermission rolePerm =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType
			).orElse(
				new RolePermission(roleId, resourceType)
			);

		rolePerm.setPermissionsMask(mask);
		_rolePermissionRepository.save(rolePerm);
	}

	private final ResourceActionRepository _resourceActionRepository;
	private final RolePermissionRepository _rolePermissionRepository;

}