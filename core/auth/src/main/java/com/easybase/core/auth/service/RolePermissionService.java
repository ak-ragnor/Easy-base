/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service;

import com.easybase.core.auth.domain.entity.RolePermission;

import java.util.List;
import java.util.UUID;

/**
 * External-facing service interface for role permission operations.
 * Performs permission checks before delegating to RolePermissionLocalService.
 * Never performs persistence directly - always delegates to RolePermissionLocalService.
 *
 * @author Akhash R
 */
public interface RolePermissionService {

	/**
	 * Add a permission to a role for a specific resource type.
	 * Requires PERMISSION:UPDATE permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param bitValue the permission bit value to add
	 * @return the updated role permission
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public RolePermission addPermission(
		UUID roleId, String resourceType, int bitValue);

	/**
	 * Add multiple permissions to a role for a specific resource type.
	 * Requires PERMISSION:UPDATE permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param bitValues the permission bit values to add
	 * @return the updated role permission
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public RolePermission addPermissions(
		UUID roleId, String resourceType, int... bitValues);

	/**
	 * Check if a role has all specified permissions for a resource type.
	 * Requires PERMISSION:VIEW permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param actionKeys list of action keys to check
	 * @return true if the role has ALL specified permissions
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 * @throws IllegalArgumentException if any action is invalid or inactive
	 */
	public boolean checkPermissions(
		UUID roleId, String resourceType, List<String> actionKeys);

	/**
	 * Clear all permissions for a role and resource type.
	 * Requires PERMISSION:UPDATE permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public void clearPermissions(UUID roleId, String resourceType);

	/**
	 * Create or update role permissions for a specific resource type.
	 * Requires PERMISSION:UPDATE permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param permissionsMask the complete permissions mask to set
	 * @return the created or updated role permission
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public RolePermission createOrUpdateRolePermission(
		UUID roleId, String resourceType, long permissionsMask);

	/**
	 * Delete all permissions for a role.
	 * Requires PERMISSION:DELETE permission.
	 *
	 * @param roleId the role ID
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public void deleteAllPermissionsForRole(UUID roleId);

	/**
	 * Delete permissions for a role and specific resource type.
	 * Requires PERMISSION:DELETE permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public void deletePermissionsForRoleAndResource(
		UUID roleId, String resourceType);

	/**
	 * Get all role permissions for a specific resource type.
	 * Requires PERMISSION:VIEW permission.
	 *
	 * @param resourceType the resource type
	 * @return list of role permissions
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public List<RolePermission> getPermissionsByResourceType(
		String resourceType);

	/**
	 * Get all permissions for a role.
	 * Requires PERMISSION:VIEW permission.
	 *
	 * @param roleId the role ID
	 * @return list of role permissions
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public List<RolePermission> getPermissionsForRole(UUID roleId);

	/**
	 * Get permissions for a role and specific resource type.
	 * Requires PERMISSION:VIEW permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @return the role permission or null if not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public RolePermission getPermissionsForRoleAndResource(
		UUID roleId, String resourceType);

	/**
	 * Get all permissions for multiple roles.
	 * Requires PERMISSION:VIEW permission.
	 *
	 * @param roleIds the list of role IDs
	 * @return list of role permissions
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public List<RolePermission> getPermissionsForRoles(List<UUID> roleIds);

	/**
	 * Get permissions for multiple roles and a specific resource type.
	 * Requires PERMISSION:VIEW permission.
	 *
	 * @param roleIds the list of role IDs
	 * @param resourceType the resource type
	 * @return list of role permissions
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public List<RolePermission> getPermissionsForRolesAndResource(
		List<UUID> roleIds, String resourceType);

	/**
	 * Grant (add) permissions to a role using action keys.
	 * Requires PERMISSION:UPDATE permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param actionKeys list of action keys to grant
	 * @return the updated role permission
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 * @throws IllegalArgumentException if any action is invalid or inactive
	 */
	public RolePermission grantPermissionsByActionKeys(
		UUID roleId, String resourceType, List<String> actionKeys);

	/**
	 * Check if any of the given roles has a specific permission for a resource type.
	 * Requires PERMISSION:VIEW permission.
	 *
	 * @param roleIds the list of role IDs
	 * @param resourceType the resource type
	 * @param bitValue the permission bit value to check
	 * @return true if any role has the permission
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public boolean hasPermission(
		List<UUID> roleIds, String resourceType, int bitValue);

	/**
	 * Check if a role has a specific permission for a resource type.
	 * Requires PERMISSION:VIEW permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param bitValue the permission bit value to check
	 * @return true if the role has the permission
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public boolean hasPermission(
		UUID roleId, String resourceType, int bitValue);

	// High-level action key based methods

	/**
	 * Remove a permission from a role for a specific resource type.
	 * Requires PERMISSION:UPDATE permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param bitValue the permission bit value to remove
	 * @return the updated role permission
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public RolePermission removePermission(
		UUID roleId, String resourceType, int bitValue);

	/**
	 * Remove multiple permissions from a role for a specific resource type.
	 * Requires PERMISSION:UPDATE permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param bitValues the permission bit values to remove
	 * @return the updated role permission
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public RolePermission removePermissions(
		UUID roleId, String resourceType, int... bitValues);

	/**
	 * Revoke (remove) permissions from a role using action keys.
	 * Requires PERMISSION:UPDATE permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param actionKeys list of action keys to revoke
	 * @return the updated role permission
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 * @throws IllegalArgumentException if any action is invalid or inactive
	 */
	public RolePermission revokePermissionsByActionKeys(
		UUID roleId, String resourceType, List<String> actionKeys);

	/**
	 * Set (replace) all permissions for a role using action keys.
	 * Requires PERMISSION:UPDATE permission.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param actionKeys list of action keys to set (replaces all existing)
	 * @return the updated role permission
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 * @throws IllegalArgumentException if any action is invalid or inactive
	 */
	public RolePermission setPermissionsByActionKeys(
		UUID roleId, String resourceType, List<String> actionKeys);

}