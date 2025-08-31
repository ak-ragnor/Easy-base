/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.data.engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CollectionDto {

	@SuppressWarnings("unused")
	private List<AttributeDto> attributes;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime createdAt;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@NotBlank(message = "Collection name is required")
	@Pattern(
		message = "Collection name must start with a letter and contain only alphanumeric characters and underscores",
		regexp = "^[a-zA-Z][a-zA-Z0-9_]*$"
	)
	@Size(
		max = 63,
		message = "Collection name must be between 1 and 63 characters", min = 1
	)
	private String name;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updatedAt;

}