/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.role.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user role assignment.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserRoleAssignmentDto {

	private Instant assignedAt;
	private UUID assignedBy;
	private Instant expiresAt;
	private boolean isActive;

	@NotNull
	private UUID roleId;

	private UUID tenantId;

	@NotNull
	private UUID userId;

}