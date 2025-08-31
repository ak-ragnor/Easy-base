/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import lombok.Data;

@Data
public class LoginRequest {

	@Email
	@NotBlank
	private String email;

	@NotBlank
	private String password;

	@NotNull
	private UUID tenantId;

}