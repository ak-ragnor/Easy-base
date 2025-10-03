/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.domain;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Permission context interface providing access to current user permissions and roles
 * within the scope of the current request/session. Manages user permissions and roles
 * independently from ServiceContext for clean separation of concerns.
 *
 * @author Akhash R
 */
public interface PermissionContext {

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
	 * Checks if user has all of the specified roles.
	 *
	 * @param roleNames the role names to check
	 * @return true if user has all roles
	 */
	public default boolean hasAllRoles(String... roleNames) {
		List<String> userRoles = roles();

		for (String roleName : roleNames) {
			if (!userRoles.contains(roleName)) {
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

	/**
	 * Checks if user has any of the specified roles.
	 *
	 * @param roleNames the role names to check
	 * @return true if user has at least one role
	 */
	public default boolean hasAnyRole(String... roleNames) {
		List<String> userRoles = roles();

		for (String roleName : roleNames) {
			if (userRoles.contains(roleName)) {
				return true;
			}
		}

		return false;
	}

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
	 * Checks if user has a specific role.
	 *
	 * @param roleName the role name to check
	 * @return true if user has the role
	 */
	public default boolean hasRole(String roleName) {
		return roles().contains(roleName);
	}

	/**
	 * Gets all permissions for the current user.
	 * Loaded during authentication for performance.
	 *
	 * @return set of permission keys (e.g., "ROLE:CREATE", "USER:UPDATE")
	 */
	public Set<String> permissions();

	/**
	 * Gets all roles for the current user.
	 * Loaded during authentication for performance.
	 *
	 * @return list of role names (e.g., "ADMIN", "USER", "MANAGER")
	 */
	public List<String> roles();

	/**
	 * Gets the tenant ID this permission context belongs to.
	 * May be null for system-wide permissions.
	 *
	 * @return the tenant ID, may be null
	 */
	public UUID tenantId();

	/**
	 * Gets the user ID this permission context belongs to.
	 *
	 * @return the user ID
	 */
	public UUID userId();

}