/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for permission operations on a specific resource type.
 * Represents actions for a single resource type (e.g., USER, PERMISSION, etc.).
 *
 * Example:
 * {
 *   "resourceType": "user",
 *   "actions": ["create", "read", "update"]
 * }
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class PermissionDto {

	/**
	 * List of action keys (e.g., ["create", "read", "update"]).
	 * Used for both requests (grant/revoke/set) and responses (granted actions).
	 */
	@NotEmpty
	private List<@NotBlank String> actions;

	/**
	 * Whether the role has ALL the requested permissions.
	 * READ_ONLY - only returned in check operation responses, ignored in requests.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Boolean hasPermission;

	/**
	 * The resource type (e.g., "USER", "PERMISSION", "DATA").
	 */
	@NotBlank
	@Size(max = 50)
	private String resourceType;

}