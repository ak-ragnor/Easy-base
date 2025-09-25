/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.domain;

import java.util.Set;
import java.util.UUID;

/**
 * Permission context interface providing access to current user permissions
 * within the scope of the current request/session. Manages user permissions
 * independently from ServiceContext for clean separation of concerns.
 *
 * @author Akhash R
 */
public interface PermissionContext {

	/**
	 * Gets all permissions for the current user.
	 * Loaded during authentication for performance.
	 *
	 * @return set of permission keys (e.g., "ROLE:CREATE", "USER:UPDATE")
	 */
	public Set<String> permissions();

	/**
	 * Gets the user ID this permission context belongs to.
	 *
	 * @return the user ID
	 */
	public UUID userId();

	/**
	 * Gets the tenant ID this permission context belongs to.
	 * May be null for system-wide permissions.
	 *
	 * @return the tenant ID, may be null
	 */
	public UUID tenantId();

	/**
	 * Convenience method to check if user has specific permission.
	 *
	 * @param permissionKey the permission to check
	 * @return true if user has the permission
	 */
	public default boolean hasPermission(String permissionKey) {
		return permissions().contains(permissionKey);
	}

	/**
	 * Checks if user has all of the specified permissions.
	 *
	 * @param permissionKeys the permission keys to check
	 * @return true if user has all permissions
	 */
	public default boolean hasAllPermissions(String... permissionKeys) {
		Set<String> userPermissions = permissions();
		for (String permission : permissionKeys) {
			if (!userPermissions.contains(permission)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if user has any of the specified permissions.
	 *
	 * @param permissionKeys the permission keys to check
	 * @return true if user has at least one permission
	 */
	public default boolean hasAnyPermission(String... permissionKeys) {
		Set<String> userPermissions = permissions();
		for (String permission : permissionKeys) {
			if (userPermissions.contains(permission)) {
				return true;
			}
		}
		return false;
	}

}