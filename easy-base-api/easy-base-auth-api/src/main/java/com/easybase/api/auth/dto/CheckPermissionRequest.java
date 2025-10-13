/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for checking if a role has a specific permission using action key.
 * Example: permissionKey = "user.create"
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CheckPermissionRequest {

	/**
	 * The permission key to check (e.g., "user.create", "data.read").
	 * Format: "{resourceType}.{action}"
	 */
	@NotBlank
	private String permissionKey;

	@NotNull
	private UUID roleId;

}