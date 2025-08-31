/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.dto;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class TokenResponse {

	@SuppressWarnings("unused")
	private String accessToken;

	@SuppressWarnings("unused")
	private Instant expiresAt;

	@SuppressWarnings("unused")
	private Long expiresIn;

	@SuppressWarnings("unused")
	private String refreshToken;

	@SuppressWarnings("unused")
	private UUID tenantId;

	@SuppressWarnings("unused")
	private String tokenType;

	@SuppressWarnings("unused")
	private String userDisplayName;

	@SuppressWarnings("unused")
	private String userEmail;

	@SuppressWarnings("unused")
	private UUID userId;

}