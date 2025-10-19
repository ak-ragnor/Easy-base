/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service;

import com.easybase.core.auth.entity.RolePermission;

import java.util.List;
import java.util.UUID;

/**
 * Local service interface for role permission business logic.
 * Contains all business logic, repository calls, and transaction management.
 * Does NOT perform permission checks - that's the responsibility of RolePermissionService.
 *
 * @author Akhash R
 */
public interface RolePermissionLocalService {

	/**
	 * Add a permission to a role for a specific resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param bitValue the permission bit value to add
	 * @return the updated role permission
	 */
	public RolePermission addPermission(
		UUID roleId, String resourceType, int bitValue);

	/**
	 * Add multiple permissions to a role for a specific resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param bitValues the permission bit values to add
	 * @return the updated role permission
	 */
	public RolePermission addPermissions(
		UUID roleId, String resourceType, int... bitValues);

	/**
	 * Clear all permissions for a role and resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 */
	public void clearPermissions(UUID roleId, String resourceType);

	/**
	 * Create or update role permissions for a specific resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param permissionsMask the complete permissions mask to set
	 * @return the created or updated role permission
	 */
	public RolePermission createOrUpdateRolePermission(
		UUID roleId, String resourceType, long permissionsMask);

	/**
	 * Delete all permissions for a role.
	 *
	 * @param roleId the role ID
	 */
	public void deleteAllPermissionsForRole(UUID roleId);

	/**
	 * Delete permissions for a role and specific resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 */
	public void deletePermissionsForRoleAndResource(
		UUID roleId, String resourceType);

	/**
	 * Get all role permissions for a specific resource type.
	 *
	 * @param resourceType the resource type
	 * @return list of role permissions
	 */
	public List<RolePermission> getPermissionsByResourceType(
		String resourceType);

	/**
	 * Get all permissions for a role.
	 *
	 * @param roleId the role ID
	 * @return list of role permissions
	 */
	public List<RolePermission> getPermissionsForRole(UUID roleId);

	/**
	 * Get permissions for a role and specific resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @return the role permission or null if not found
	 */
	public RolePermission getPermissionsForRoleAndResource(
		UUID roleId, String resourceType);

	/**
	 * Get all permissions for multiple roles.
	 *
	 * @param roleIds the list of role IDs
	 * @return list of role permissions
	 */
	public List<RolePermission> getPermissionsForRoles(List<UUID> roleIds);

	/**
	 * Get permissions for multiple roles and a specific resource type.
	 *
	 * @param roleIds the list of role IDs
	 * @param resourceType the resource type
	 * @return list of role permissions
	 */
	public List<RolePermission> getPermissionsForRolesAndResource(
		List<UUID> roleIds, String resourceType);

	/**
	 * Check if any of the given roles has a specific permission for a resource type.
	 *
	 * @param roleIds the list of role IDs
	 * @param resourceType the resource type
	 * @param bitValue the permission bit value to check
	 * @return true if any role has the permission
	 */
	public boolean hasPermission(
		List<UUID> roleIds, String resourceType, int bitValue);

	/**
	 * Check if a role has a specific permission for a resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param bitValue the permission bit value to check
	 * @return true if the role has the permission
	 */
	public boolean hasPermission(
		UUID roleId, String resourceType, int bitValue);

	/**
	 * Remove a permission from a role for a specific resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param bitValue the permission bit value to remove
	 * @return the updated role permission
	 */
	public RolePermission removePermission(
		UUID roleId, String resourceType, int bitValue);

	/**
	 * Remove multiple permissions from a role for a specific resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param bitValues the permission bit values to remove
	 * @return the updated role permission
	 */
	public RolePermission removePermissions(
		UUID roleId, String resourceType, int... bitValues);

}