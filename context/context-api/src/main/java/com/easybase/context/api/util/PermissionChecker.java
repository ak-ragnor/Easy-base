/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.util;

import com.easybase.common.exception.ForbiddenException;
import com.easybase.context.api.domain.PermissionContext;
import com.easybase.infrastructure.auth.constants.SystemRoles;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * Utility component for checking permissions using PermissionContext.
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class PermissionChecker {

	/**
	 * Check permission and throw exception if denied.
	 * ADMIN users automatically pass all checks.
	 *
	 * @param permissionKey the permission key to check
	 * @throws ForbiddenException if permission is denied
	 */
	public void check(String permissionKey) {
		if (!hasPermission(permissionKey)) {
			throw new ForbiddenException("Access denied: " + permissionKey);
		}
	}

	/**
	 * Check all permissions and throw exception if any is denied.
	 *
	 * @param permissionKeys the permission keys to check
	 * @throws ForbiddenException if any permission is denied
	 */
	public void checkAll(String... permissionKeys) {
		if (!hasAllPermissions(permissionKeys)) {
			throw new ForbiddenException(
				"Access denied: Missing one or more permissions: " +
					Arrays.toString(permissionKeys));
		}
	}

	/**
	 * Check any permissions and throw exception if none is granted.
	 *
	 * @param permissionKeys the permission keys to check
	 * @throws ForbiddenException if no permission is granted
	 */
	public void checkAny(String... permissionKeys) {
		if (!hasAnyPermission(permissionKeys)) {
			throw new ForbiddenException(
				"Access denied: None of the required permissions granted: " +
					Arrays.toString(permissionKeys));
		}
	}

	/**
	 * Check if current user has all of the specified permissions.
	 * ADMIN users automatically have all permissions.
	 *
	 * @param permissionKeys the permission keys to check
	 * @return true if user has all permissions or is an ADMIN
	 */
	public boolean hasAllPermissions(String... permissionKeys) {
		if (_permissionContext.hasRole(_ADMIN_ROLE)) {
			return true;
		}

		return _permissionContext.hasAllPermissions(permissionKeys);
	}

	/**
	 * Check if current user has any of the specified permissions.
	 * ADMIN users automatically have all permissions.
	 *
	 * @param permissionKeys the permission keys to check
	 * @return true if user has at least one permission or is an ADMIN
	 */
	public boolean hasAnyPermission(String... permissionKeys) {
		if (_permissionContext.hasRole(_ADMIN_ROLE)) {
			return true;
		}

		return _permissionContext.hasAnyPermission(permissionKeys);
	}

	/**
	 * Check if current user has the specified permission.
	 * ADMIN users automatically have all permissions.
	 *
	 * @param permissionKey the permission key to check
	 * @return true if user has the permission or is an ADMIN
	 */
	public boolean hasPermission(String permissionKey) {
		if (_permissionContext.hasRole(_ADMIN_ROLE)) {
			return true;
		}

		return _permissionContext.hasPermission(permissionKey);
	}

	private static final String _ADMIN_ROLE = SystemRoles.ADMIN;

	private final PermissionContext _permissionContext;

}