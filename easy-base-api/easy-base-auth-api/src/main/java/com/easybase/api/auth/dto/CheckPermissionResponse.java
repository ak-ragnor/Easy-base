/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for permission check results using action keys.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CheckPermissionResponse {

	/**
	 * Whether the role has the requested permission.
	 */
	private boolean hasPermission;

	/**
	 * The permission key that was checked (e.g., "user.create").
	 */
	private String permissionKey;

}