/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service.impl;

import com.easybase.core.auth.entity.RolePermission;
import com.easybase.core.auth.repository.RolePermissionRepository;
import com.easybase.core.auth.service.RolePermissionLocalService;
import com.easybase.core.auth.util.BitMaskUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of RolePermissionLocalService.
 * Contains all business logic for role permission operations.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Transactional
public class RolePermissionLocalServiceImpl
	implements RolePermissionLocalService {

	@Override
	public RolePermission addPermission(
		UUID roleId, String resourceType, int bitValue) {

		RolePermission rolePermission = _getOrCreateRolePermission(
			roleId, resourceType);

		rolePermission.addPermission(bitValue);

		return _rolePermissionRepository.save(rolePermission);
	}

	@Override
	public RolePermission addPermissions(
		UUID roleId, String resourceType, int... bitValues) {

		RolePermission rolePermission = _getOrCreateRolePermission(
			roleId, resourceType);

		rolePermission.addPermissions(bitValues);

		return _rolePermissionRepository.save(rolePermission);
	}

	@Override
	public void clearPermissions(UUID roleId, String resourceType) {
		Optional<RolePermission> rolePermissionOpt =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType);

		if (rolePermissionOpt.isPresent()) {
			RolePermission rolePermission = rolePermissionOpt.get();

			rolePermission.clearPermissions();

			_rolePermissionRepository.save(rolePermission);
		}
	}

	@Override
	public RolePermission createOrUpdateRolePermission(
		UUID roleId, String resourceType, long permissionsMask) {

		RolePermission rolePermission = _getOrCreateRolePermission(
			roleId, resourceType);

		rolePermission.setPermissionsMask(permissionsMask);

		return _rolePermissionRepository.save(rolePermission);
	}

	@Override
	public void deleteAllPermissionsForRole(UUID roleId) {
		_rolePermissionRepository.deleteByRoleId(roleId);
	}

	@Override
	public void deletePermissionsForRoleAndResource(
		UUID roleId, String resourceType) {

		_rolePermissionRepository.deleteByRoleIdAndResourceType(
			roleId, resourceType);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RolePermission> getPermissionsByResourceType(
		String resourceType) {

		return _rolePermissionRepository.findByResourceType(resourceType);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RolePermission> getPermissionsForRole(UUID roleId) {
		return _rolePermissionRepository.findByRoleId(roleId);
	}

	@Override
	@Transactional(readOnly = true)
	public RolePermission getPermissionsForRoleAndResource(
		UUID roleId, String resourceType) {

		Optional<RolePermission> rolePermissionOptional =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType);

		if (rolePermissionOptional.isPresent()) {
			return rolePermissionOptional.get();
		}

		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RolePermission> getPermissionsForRoles(List<UUID> roleIds) {
		return _rolePermissionRepository.findByRoleIdIn(roleIds);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RolePermission> getPermissionsForRolesAndResource(
		List<UUID> roleIds, String resourceType) {

		return _rolePermissionRepository.findByRoleIdInAndResourceType(
			roleIds, resourceType);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasPermission(
		List<UUID> roleIds, String resourceType, int bitValue) {

		List<RolePermission> permissions =
			_rolePermissionRepository.findByRoleIdInAndResourceType(
				roleIds, resourceType);

		for (RolePermission permission : permissions) {
			if (BitMaskUtil.hasBit(permission.getPermissionsMask(), bitValue)) {
				return true;
			}
		}

		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasPermission(
		UUID roleId, String resourceType, int bitValue) {

		Optional<RolePermission> rolePermissionOpt =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType);

		if (rolePermissionOpt.isEmpty()) {
			return false;
		}

		return BitMaskUtil.hasBit(
			rolePermissionOpt.get(
			).getPermissionsMask(),
			bitValue);
	}

	@Override
	public RolePermission removePermission(
		UUID roleId, String resourceType, int bitValue) {

		Optional<RolePermission> rolePermissionOpt =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType);

		if (rolePermissionOpt.isEmpty()) {
			return null;
		}

		RolePermission rolePermission = rolePermissionOpt.get();

		rolePermission.removePermission(bitValue);

		return _rolePermissionRepository.save(rolePermission);
	}

	@Override
	public RolePermission removePermissions(
		UUID roleId, String resourceType, int... bitValues) {

		Optional<RolePermission> rolePermissionOpt =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType);

		if (rolePermissionOpt.isEmpty()) {
			return null;
		}

		RolePermission rolePermission = rolePermissionOpt.get();

		rolePermission.removePermissions(bitValues);

		return _rolePermissionRepository.save(rolePermission);
	}

	private RolePermission _getOrCreateRolePermission(
		UUID roleId, String resourceType) {

		Optional<RolePermission> rolePermissionOptional =
			_rolePermissionRepository.findByRoleIdAndResourceType(
				roleId, resourceType);

		if (rolePermissionOptional.isPresent()) {
			return rolePermissionOptional.get();
		}

		return new RolePermission(roleId, resourceType);
	}

	private final RolePermissionRepository _rolePermissionRepository;

}