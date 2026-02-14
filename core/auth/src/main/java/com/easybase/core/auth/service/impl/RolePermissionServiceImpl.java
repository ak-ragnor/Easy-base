/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service.impl;

import com.easybase.context.api.util.PermissionChecker;
import com.easybase.core.auth.action.PermissionActions;
import com.easybase.core.auth.domain.entity.RolePermission;
import com.easybase.core.auth.helper.PermissionHelper;
import com.easybase.core.auth.service.RolePermissionLocalService;
import com.easybase.core.auth.service.RolePermissionService;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Implementation of {@link RolePermissionService}.
 * ALWAYS performs permission checks before delegating to RolePermissionLocalService.
 * Never performs persistence directly - always delegates to RolePermissionLocalService.
 *
 * <p>If permission checks are not needed, use RolePermissionLocalService directly.</p>
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

	@Override
	public RolePermission addPermission(
		UUID roleId, String resourceType, int bitValue) {

		_permissionChecker.check(PermissionActions.UPDATE);

		return _rolePermissionLocalService.addPermission(
			roleId, resourceType, bitValue);
	}

	@Override
	public RolePermission addPermissions(
		UUID roleId, String resourceType, int... bitValues) {

		_permissionChecker.check(PermissionActions.UPDATE);

		return _rolePermissionLocalService.addPermissions(
			roleId, resourceType, bitValues);
	}

	@Override
	public boolean checkPermissions(
		UUID roleId, String resourceType, List<String> actionKeys) {

		_permissionChecker.check(PermissionActions.VIEW);

		int[] bitValues = _permissionHelper.convertActionKeysToBitValues(
			resourceType, actionKeys);

		for (int bitValue : bitValues) {
			boolean has = _rolePermissionLocalService.hasPermission(
				roleId, resourceType, bitValue);

			if (!has) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void clearPermissions(UUID roleId, String resourceType) {
		_permissionChecker.check(PermissionActions.UPDATE);

		_rolePermissionLocalService.clearPermissions(roleId, resourceType);
	}

	@Override
	public RolePermission createOrUpdateRolePermission(
		UUID roleId, String resourceType, long permissionsMask) {

		_permissionChecker.check(PermissionActions.UPDATE);

		return _rolePermissionLocalService.createOrUpdateRolePermission(
			roleId, resourceType, permissionsMask);
	}

	@Override
	public void deleteAllPermissionsForRole(UUID roleId) {
		_permissionChecker.check(PermissionActions.DELETE);

		_rolePermissionLocalService.deleteAllPermissionsForRole(roleId);
	}

	@Override
	public void deletePermissionsForRoleAndResource(
		UUID roleId, String resourceType) {

		_permissionChecker.check(PermissionActions.DELETE);

		_rolePermissionLocalService.deletePermissionsForRoleAndResource(
			roleId, resourceType);
	}

	@Override
	public List<RolePermission> getPermissionsByResourceType(
		String resourceType) {

		_permissionChecker.check(PermissionActions.VIEW);

		return _rolePermissionLocalService.getPermissionsByResourceType(
			resourceType);
	}

	@Override
	public List<RolePermission> getPermissionsForRole(UUID roleId) {
		_permissionChecker.check(PermissionActions.VIEW);

		return _rolePermissionLocalService.getPermissionsForRole(roleId);
	}

	@Override
	public RolePermission getPermissionsForRoleAndResource(
		UUID roleId, String resourceType) {

		_permissionChecker.check(PermissionActions.VIEW);

		return _rolePermissionLocalService.getPermissionsForRoleAndResource(
			roleId, resourceType);
	}

	@Override
	public List<RolePermission> getPermissionsForRoles(List<UUID> roleIds) {
		_permissionChecker.check(PermissionActions.VIEW);

		return _rolePermissionLocalService.getPermissionsForRoles(roleIds);
	}

	@Override
	public List<RolePermission> getPermissionsForRolesAndResource(
		List<UUID> roleIds, String resourceType) {

		_permissionChecker.check(PermissionActions.VIEW);

		return _rolePermissionLocalService.getPermissionsForRolesAndResource(
			roleIds, resourceType);
	}

	@Override
	public RolePermission grantPermissionsByActionKeys(
		UUID roleId, String resourceType, List<String> actionKeys) {

		_permissionChecker.check(PermissionActions.UPDATE);

		int[] bitValues = _permissionHelper.convertActionKeysToBitValues(
			resourceType, actionKeys);

		return _rolePermissionLocalService.addPermissions(
			roleId, resourceType, bitValues);
	}

	@Override
	public boolean hasPermission(
		List<UUID> roleIds, String resourceType, int bitValue) {

		_permissionChecker.check(PermissionActions.VIEW);

		return _rolePermissionLocalService.hasPermission(
			roleIds, resourceType, bitValue);
	}

	@Override
	public boolean hasPermission(
		UUID roleId, String resourceType, int bitValue) {

		_permissionChecker.check(PermissionActions.VIEW);

		return _rolePermissionLocalService.hasPermission(
			roleId, resourceType, bitValue);
	}

	@Override
	public RolePermission removePermission(
		UUID roleId, String resourceType, int bitValue) {

		_permissionChecker.check(PermissionActions.UPDATE);

		return _rolePermissionLocalService.removePermission(
			roleId, resourceType, bitValue);
	}

	@Override
	public RolePermission removePermissions(
		UUID roleId, String resourceType, int... bitValues) {

		_permissionChecker.check(PermissionActions.UPDATE);

		return _rolePermissionLocalService.removePermissions(
			roleId, resourceType, bitValues);
	}

	@Override
	public RolePermission revokePermissionsByActionKeys(
		UUID roleId, String resourceType, List<String> actionKeys) {

		_permissionChecker.check(PermissionActions.UPDATE);

		int[] bitValues = _permissionHelper.convertActionKeysToBitValues(
			resourceType, actionKeys);

		return _rolePermissionLocalService.removePermissions(
			roleId, resourceType, bitValues);
	}

	@Override
	public RolePermission setPermissionsByActionKeys(
		UUID roleId, String resourceType, List<String> actionKeys) {

		_permissionChecker.check(PermissionActions.UPDATE);

		long permissionsMask = _permissionHelper.calculatePermissionMask(
			resourceType, actionKeys);

		return _rolePermissionLocalService.createOrUpdateRolePermission(
			roleId, resourceType, permissionsMask);
	}

	private final PermissionChecker _permissionChecker;
	private final PermissionHelper _permissionHelper;
	private final RolePermissionLocalService _rolePermissionLocalService;

}