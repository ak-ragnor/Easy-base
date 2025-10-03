/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Permission entity.
 *
 * @deprecated Replaced by {@link ResourceActionDto} and {@link RolePermissionDto}.
 * Use ResourceActionDto for action definitions and RolePermissionDto for role-based permissions.
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@Deprecated
@NoArgsConstructor
public class PermissionDto {

	@NotBlank
	@Size(max = 50)
	private String action;

	private Instant createdAt;

	@Size(max = 255)
	private String description;

	private UUID id;

	@NotBlank
	@Size(max = 100)
	private String permissionKey;

	@NotBlank
	@Size(max = 50)
	private String resourceType;

	private Instant updatedAt;

}