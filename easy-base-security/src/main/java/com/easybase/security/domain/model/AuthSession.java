/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.domain.model;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class AuthSession {

	@SuppressWarnings("unused")
	private Instant createdAt;

	@SuppressWarnings("unused")
	private Instant expiresAt;

	@SuppressWarnings("unused")
	private UUID id;

	@SuppressWarnings("unused")
	private String ipAddress;

	@SuppressWarnings("unused")
	private boolean revoked;

	@SuppressWarnings("unused")
	private String sessionToken;

	@SuppressWarnings("unused")
	private UUID tenantId;

	@SuppressWarnings("unused")
	private Instant updatedAt;

	@SuppressWarnings("unused")
	private String userAgent;

	@SuppressWarnings("unused")
	private UUID userId;

}