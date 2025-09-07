/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.domain.model;

import java.time.Instant;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

/**
 * Domain model representing an authentication token response.
 * Contains the essential information for a successful authentication.
 *
 * @author Akhash R
 */
@Builder
@Data
@SuppressWarnings("unused")
public class AuthToken {

	@SuppressWarnings("unused")
	private String accessToken;

	@SuppressWarnings("unused")
	private Instant expiresAt;

	@SuppressWarnings("unused")
	private long expiresIn;

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