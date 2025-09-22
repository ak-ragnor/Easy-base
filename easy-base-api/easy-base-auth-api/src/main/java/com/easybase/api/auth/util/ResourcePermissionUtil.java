/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.util;

import com.easybase.api.auth.service.PermissionChecker;
import com.easybase.common.exception.ForbiddenException;

import java.util.UUID;

/**
 * Utility class for checking resource permissions.
 * Provides static methods for permission checking as requested.
 *
 * Usage example:
 * ResourcePermissionUtil.check(getPermissionChecker(), tenantId, ActionKeys.ADD_COLLECTION);
 *
 * @author Akhash R
 */
public final class ResourcePermissionUtil {

	/**
	 * Check permission and throw exception if denied.
	 *
	 * @param permissionChecker the permission checker instance
	 * @param permissionKey the permission key to check
	 * @throws ForbiddenException if permission is denied
	 */
	public static void check(
		PermissionChecker permissionChecker, String permissionKey) {

		var result = permissionChecker.hasPermission(permissionKey);

		if (!result.isHasPermission()) {
			throw new ForbiddenException(
				"Access denied: " + result.getReason());
		}
	}

	/**
	 * Check permission within tenant context and throw exception if denied.
	 *
	 * @param permissionChecker the permission checker instance
	 * @param tenantId the tenant ID (can be null for system-wide permissions)
	 * @param permissionKey the permission key to check
	 * @throws ForbiddenException if permission is denied
	 */
	public static void check(
		PermissionChecker permissionChecker, UUID tenantId,
		String permissionKey) {

		var result = tenantId != null ?
			permissionChecker.hasPermission(tenantId, permissionKey) :
				permissionChecker.hasPermission(permissionKey);

		if (!result.isHasPermission()) {
			throw new ForbiddenException(
				"Access denied: " + result.getReason());
		}
	}

	/**
	 * Check permission for specific user and throw exception if denied.
	 *
	 * @param permissionChecker the permission checker instance
	 * @param userId the user ID
	 * @param permissionKey the permission key to check
	 * @throws ForbiddenException if permission is denied
	 */
	public static void check(
		PermissionChecker permissionChecker, UUID userId,
		String permissionKey) {

		var result = permissionChecker.hasPermission(userId, permissionKey);

		if (!result.isHasPermission()) {
			throw new ForbiddenException(
				"Access denied: " + result.getReason());
		}
	}

	/**
	 * Check permission for specific user within tenant context and throw exception if denied.
	 *
	 * @param permissionChecker the permission checker instance
	 * @param userId the user ID
	 * @param tenantId the tenant ID
	 * @param permissionKey the permission key to check
	 * @throws ForbiddenException if permission is denied
	 */
	public static void check(
		PermissionChecker permissionChecker, UUID userId, UUID tenantId,
		String permissionKey) {

		var result = permissionChecker.hasPermission(
			userId, tenantId, permissionKey);

		if (!result.isHasPermission()) {
			throw new ForbiddenException(
				"Access denied: " + result.getReason());
		}
	}

	/**
	 * Check if all of the specified permissions are granted.
	 *
	 * @param permissionChecker the permission checker instance
	 * @param permissionKeys the permission keys to check
	 * @return true if all permissions are granted
	 */
	public static boolean hasAllPermissions(
		PermissionChecker permissionChecker, String... permissionKeys) {

		return permissionChecker.hasAllPermissions(permissionKeys);
	}

	/**
	 * Check if any of the specified permissions are granted.
	 *
	 * @param permissionChecker the permission checker instance
	 * @param permissionKeys the permission keys to check
	 * @return true if at least one permission is granted
	 */
	public static boolean hasAnyPermission(
		PermissionChecker permissionChecker, String... permissionKeys) {

		return permissionChecker.hasAnyPermission(permissionKeys);
	}

	/**
	 * Check if permission is granted without throwing exception.
	 *
	 * @param permissionChecker the permission checker instance
	 * @param permissionKey the permission key to check
	 * @return true if permission is granted, false otherwise
	 */
	public static boolean hasPermission(
		PermissionChecker permissionChecker, String permissionKey) {

		return permissionChecker.hasPermission(
			permissionKey
		).isHasPermission();
	}

	/**
	 * Check if permission is granted within tenant context without throwing exception.
	 *
	 * @param permissionChecker the permission checker instance
	 * @param tenantId the tenant ID
	 * @param permissionKey the permission key to check
	 * @return true if permission is granted, false otherwise
	 */
	public static boolean hasPermission(
		PermissionChecker permissionChecker, UUID tenantId,
		String permissionKey) {

		var result = tenantId != null ?
			permissionChecker.hasPermission(tenantId, permissionKey) :
				permissionChecker.hasPermission(permissionKey);

		return result.isHasPermission();
	}

	private ResourcePermissionUtil() {
		throw new UnsupportedOperationException("Utility class");
	}

}