/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of a permission check operation.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class PermissionCheckResult {

	public static PermissionCheckResult allowed(String permissionKey) {
		return new PermissionCheckResult(
			true, permissionKey, "Permission granted");
	}

	public static PermissionCheckResult denied(
		String permissionKey, String reason) {

		return new PermissionCheckResult(false, permissionKey, reason);
	}

	private boolean hasPermission;
	private String permissionKey;
	private String reason;

}