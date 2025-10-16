/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.role.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Consolidated DTO for Role operations.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class RoleDto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Instant createdDate;

	@Size(max = 255)
	private String description;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private boolean isActive;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private boolean isSystem;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Instant lastModifiedDate;

	@NotBlank
	@Size(max = 50)
	private String name;

	private UUID tenantId;

}