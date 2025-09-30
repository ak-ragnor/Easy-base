/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto;

import lombok.Data;

/**
 * Result of a permission check operation.
 *
 * @author Akhash R
 */
@Data
public class PermissionCheckResult {

	public static PermissionCheckResult allowed(String permissionKey) {
		return new PermissionCheckResult(
			true, permissionKey, "Permission granted");
	}

	public static PermissionCheckResult denied(
		String permissionKey, String reason) {

		return new PermissionCheckResult(false, permissionKey, reason);
	}

	public PermissionCheckResult() {
	}

	public PermissionCheckResult(
		boolean hasPermission, String permissionKey, String reason) {

		this.hasPermission = hasPermission;
		this.permissionKey = permissionKey;
		this.reason = reason;
	}

	private boolean hasPermission;
	private String permissionKey;
	private String reason;

}