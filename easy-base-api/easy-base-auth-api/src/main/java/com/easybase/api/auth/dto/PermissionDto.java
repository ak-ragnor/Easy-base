/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Consolidated DTO for permission checking operations.
 * Supports checking multiple actions for a specific resource type.
 *
 * Request Example (check permissions):
 * {
 *   "roleId": "uuid",
 *   "resourceType": "user",
 *   "actions": ["create", "read", "update"]
 * }
 *
 * Response Example (with results):
 * {
 *   "roleId": "uuid",
 *   "resourceType": "user",
 *   "actions": ["create", "read", "update"],
 *   "hasPermission": true  // true if ALL actions are granted
 * }
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class PermissionDto {

	/**
	 * List of action IDs to check (e.g., ["create", "read", "update"]).
	 * These are the action keys without the resourceType prefix.
	 */
	@NotEmpty
	private List<@NotBlank String> actions;

	/**
	 * Whether the role has ALL the requested permissions.
	 * READ_ONLY - only returned in responses, ignored in requests.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Boolean hasPermission;

	/**
	 * The resource type (e.g., "user", "data", "collection").
	 */
	@NotBlank
	@Size(max = 50)
	private String resourceType;

	@NotNull
	private UUID roleId;

}