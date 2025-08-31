/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

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
public class UserDto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime createdAt;

	@Size(max = 100, message = "Display name must not exceed 100 characters")
	private String displayName;

	@Email(message = "Email should be valid")
	@NotBlank(message = "Email is required")
	@Size(max = 255, message = "Email must not exceed 255 characters")
	private String email;

	@Size(max = 100, message = "First name must not exceed 100 characters")
	private String firstName;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	@Size(max = 100, message = "Last name must not exceed 100 characters")
	private String lastName;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID tenantId;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updatedAt;

}