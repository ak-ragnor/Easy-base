/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.service.impl;

import com.easybase.context.api.util.PermissionChecker;
import com.easybase.core.role.action.RoleActions;
import com.easybase.core.role.entity.Role;
import com.easybase.core.role.entity.UserRole;
import com.easybase.core.role.service.RoleLocalService;
import com.easybase.core.role.service.RoleService;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Implementation of {@link RoleService}.
 * ALWAYS performs permission checks before delegating to RoleLocalService.
 * Never performs persistence directly - always delegates to RoleLocalService.
 *
 * <p>If permission checks are not needed, use RoleLocalService directly.</p>
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

	@Override
	public UserRole assignRoleToUser(
		UUID userId, UUID roleId, UUID tenantId, Instant expiresAt) {

		_permissionChecker.check(RoleActions.ASSIGN);

		return _roleLocalService.assignRoleToUser(
			userId, roleId, tenantId, expiresAt);
	}

	@Override
	public Role createRole(
		String name, String description, UUID tenantId, boolean system) {

		_permissionChecker.check(RoleActions.CREATE);

		return _roleLocalService.createRole(
			name, description, tenantId, system);
	}

	@Override
	public void deleteRole(UUID roleId) {
		_permissionChecker.check(RoleActions.DELETE);
		_roleLocalService.deleteRole(roleId);
	}

	@Override
	public List<Role> getAvailableRoles(UUID tenantId) {
		_permissionChecker.check(RoleActions.LIST);

		return _roleLocalService.getAvailableRoles(tenantId);
	}

	@Override
	public Role getRoleById(UUID roleId) {
		_permissionChecker.check(RoleActions.VIEW);

		return _roleLocalService.getRoleById(roleId);
	}

	@Override
	public List<UserRole> getUserRoles(UUID userId) {
		_permissionChecker.check(RoleActions.VIEW);

		return _roleLocalService.getUserRoles(userId);
	}

	@Override
	public void revokeRoleFromUser(UUID userId, UUID roleId) {
		_permissionChecker.check(RoleActions.REVOKE);
		_roleLocalService.revokeRoleFromUser(userId, roleId);
	}

	@Override
	public Role updateRole(UUID roleId, String description) {
		_permissionChecker.check(RoleActions.UPDATE);

		return _roleLocalService.updateRole(roleId, description);
	}

	private final PermissionChecker _permissionChecker;
	private final RoleLocalService _roleLocalService;

}