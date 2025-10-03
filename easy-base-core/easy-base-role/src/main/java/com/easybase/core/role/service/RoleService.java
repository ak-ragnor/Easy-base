/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.service;

import com.easybase.common.exception.ConflictException;
import com.easybase.context.core.util.PermissionChecker;
import com.easybase.core.auth.repository.RolePermissionRepository;
import com.easybase.core.role.action.RoleActions;
import com.easybase.core.role.entity.Role;
import com.easybase.core.role.entity.UserRole;
import com.easybase.core.role.repository.RoleRepository;
import com.easybase.core.role.repository.UserRoleRepository;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for role management operations with permission checks.
 * Uses injected PermissionChecker from context module for permission checking.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Transactional
public class RoleService {

	@CacheEvict(
		allEntries = true, value = {"userPermissions", "userTenantPermissions"}
	)
	public UserRole assignRoleToUser(
		UUID userId, UUID roleId, UUID tenantId, Instant expiresAt) {

		_permissionChecker.check(RoleActions.ASSIGN);

		if (_userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
			throw new ConflictException("User already has this role assigned");
		}

		_roleQueryService.getRoleById(roleId);

		UserRole userRole = new UserRole(userId, roleId, tenantId);

		userRole.setExpiresAt(expiresAt);

		return _userRoleRepository.save(userRole);
	}

	public Role createRole(
		String name, String description, UUID tenantId, boolean system) {

		_permissionChecker.check(RoleActions.CREATE);

		if (system && _roleRepository.existsByNameAndSystemTrue(name)) {
			throw new ConflictException(
				"System role with name '" + name + "' already exists");
		}

		if (!system && (tenantId != null) &&
			_roleRepository.existsByNameAndTenantId(name, tenantId)) {

			throw new ConflictException(
				"Role with name '" + name + "' already exists for this tenant");
		}

		Role role = new Role();

		role.setName(name);
		role.setDescription(description);
		role.setSystem(system);
		role.setActive(true);

		if (!system && (tenantId != null)) {
			role.setTenantId(tenantId);
		}

		return _roleRepository.save(role);
	}

	public void deleteRole(UUID roleId) {
		_permissionChecker.check(RoleActions.DELETE);

		Role role = _roleQueryService.getRoleById(roleId);

		if (role.isSystem()) {
			throw new ConflictException("Cannot delete system roles");
		}

		_rolePermissionRepository.deleteByRoleId(roleId);

		_userRoleRepository.deleteByRoleId(roleId);

		_roleRepository.delete(role);
	}

	@Transactional(readOnly = true)
	public List<Role> getAvailableRoles(UUID tenantId) {
		_permissionChecker.check(RoleActions.LIST);

		return _roleQueryService.getAvailableRoles(tenantId);
	}

	@Transactional(readOnly = true)
	public Role getRoleById(UUID roleId) {
		_permissionChecker.check(RoleActions.VIEW);

		return _roleQueryService.getRoleById(roleId);
	}

	@Transactional(readOnly = true)
	public List<UserRole> getUserRoles(UUID userId) {
		_permissionChecker.check(RoleActions.VIEW);

		return _roleQueryService.getUserRoles(userId);
	}

	@CacheEvict(
		allEntries = true, value = {"userPermissions", "userTenantPermissions"}
	)
	public void revokeRoleFromUser(UUID userId, UUID roleId) {
		_permissionChecker.check(RoleActions.REVOKE);

		_userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
	}

	public Role updateRole(UUID roleId, String description) {
		_permissionChecker.check(RoleActions.UPDATE);

		Role role = _roleQueryService.getRoleById(roleId);

		if (role.isSystem()) {
			throw new ConflictException("Cannot modify system roles");
		}

		role.setDescription(description);

		return _roleRepository.save(role);
	}

	private final PermissionChecker _permissionChecker;
	private final RolePermissionRepository _rolePermissionRepository;
	private final RoleQueryService _roleQueryService;
	private final RoleRepository _roleRepository;
	private final UserRoleRepository _userRoleRepository;

}