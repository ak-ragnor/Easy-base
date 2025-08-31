/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.data.engine.dto;

import com.easybase.core.data.engine.enums.AttributeType;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for attribute information
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class AttributeDto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@SuppressWarnings("unused")
	private boolean indexed;

	@NotBlank(message = "Attribute name is required")
	private String name;

	@NotNull(message = "Attribute type is required")
	private AttributeType type;

}