/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author Akhash R
 */
@Data
public class RefreshTokenRequest {

	@NotBlank
	private String refreshToken;

}