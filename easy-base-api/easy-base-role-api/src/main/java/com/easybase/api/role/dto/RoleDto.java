/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for role information.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class RoleDto {

	private Instant createdDate;

	@Size(max = 255)
	private String description;

	private UUID id;
	private boolean isActive;
	private boolean isSystem;
	private Instant lastModifiedDate;

	@NotBlank
	@Size(max = 50)
	private String name;

	private UUID tenantId;

}