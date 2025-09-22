/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for UserRole assignment operations.
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
	private boolean isActive;

	@NotNull
	private UUID roleId;

	private UUID tenantId;

	@NotNull
	private UUID userId;

}