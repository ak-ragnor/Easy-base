/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.service;

import com.easybase.api.auth.dto.PermissionCheckResult;

import java.util.Set;
import java.util.UUID;

/**
 * Interface for checking user permissions.
 *
 * @author Akhash R
 */
public interface PermissionChecker {

	/**
	 * Check if the current user has the specified permission.
	 *
	 * @param permissionKey the permission key (e.g., "COLLECTION:CREATE")
	 * @return permission check result
	 */
	public PermissionCheckResult hasPermission(String permissionKey);

	/**
	 * Check if the current user has the specified permission within a tenant context.
	 *
	 * @param tenantId the tenant ID
	 * @param permissionKey the permission key
	 * @return permission check result
	 */
	public PermissionCheckResult hasPermission(
		UUID tenantId, String permissionKey);

	/**
	 * Check if a specific user has the specified permission.
	 *
	 * @param userId the user ID
	 * @param permissionKey the permission key
	 * @return permission check result
	 */
	public PermissionCheckResult hasPermission(
		UUID userId, String permissionKey);

	/**
	 * Check if a specific user has the specified permission within a tenant context.
	 *
	 * @param userId the user ID
	 * @param tenantId the tenant ID
	 * @param permissionKey the permission key
	 * @return permission check result
	 */
	public PermissionCheckResult hasPermission(
		UUID userId, UUID tenantId, String permissionKey);

	/**
	 * Get all permissions for the current user.
	 *
	 * @return set of permission keys
	 */
	public Set<String> getUserPermissions();

	/**
	 * Get all permissions for the current user within a tenant context.
	 *
	 * @param tenantId the tenant ID
	 * @return set of permission keys
	 */
	public Set<String> getUserPermissions(UUID tenantId);

	/**
	 * Get all permissions for a specific user.
	 *
	 * @param userId the user ID
	 * @return set of permission keys
	 */
	public Set<String> getUserPermissions(UUID userId);

	/**
	 * Get all permissions for a specific user within a tenant context.
	 *
	 * @param userId the user ID
	 * @param tenantId the tenant ID
	 * @return set of permission keys
	 */
	public Set<String> getUserPermissions(UUID userId, UUID tenantId);

	/**
	 * Check if the current user has any of the specified permissions.
	 *
	 * @param permissionKeys the permission keys to check
	 * @return true if user has at least one of the permissions
	 */
	public boolean hasAnyPermission(String... permissionKeys);

	/**
	 * Check if the current user has all of the specified permissions.
	 *
	 * @param permissionKeys the permission keys to check
	 * @return true if user has all permissions
	 */
	public boolean hasAllPermissions(String... permissionKeys);

}