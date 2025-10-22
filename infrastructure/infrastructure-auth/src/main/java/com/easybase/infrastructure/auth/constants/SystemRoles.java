/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.auth.constants;

/**
 * System role constants for predefined roles.
 * These are global roles that exist across all tenants.
 *
 * @author Akhash R
 */
public final class SystemRoles {

	/**
	 * Administrator role with full system access
	 */
	public static final String ADMIN = "ADMIN";

	/**
	 * Guest role with limited read-only permissions
	 */
	public static final String GUEST = "GUEST";

	/**
	 * Standard authenticated user role
	 */
	public static final String USER = "USER";

	/**
	 * Check if a role name is a system role
	 *
	 * @param roleName the role name to check
	 * @return true if it's a system role, false otherwise
	 */
	public static boolean isSystemRole(String roleName) {
		if (ADMIN.equals(roleName) || USER.equals(roleName) ||
			GUEST.equals(roleName)) {

			return true;
		}

		return false;
	}

	private SystemRoles() {
		throw new UnsupportedOperationException("Utility class");
	}

}