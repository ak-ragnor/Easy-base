/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.role.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Consolidated DTO for user role assignment operations.
 * Used for assigning, unassigning, and reading user role assignments.
 *
 * - For ASSIGN (POST /api/roles/{roleId}/users): provide userId, tenantId (optional), expiresAt (optional)
 * - For UNASSIGN (DELETE /api/roles/{roleId}/users/{userId}): userId in path
 * - For READ (GET): all fields are returned
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserRoleAssignmentDto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Instant assignedAt;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID assignedBy;

	private Instant expiresAt;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private boolean isActive;

	@NotNull
	private UUID roleId;

	private UUID tenantId;

	@NotNull
	private UUID userId;

}