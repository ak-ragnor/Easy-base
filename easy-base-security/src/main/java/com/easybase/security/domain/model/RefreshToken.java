/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.domain.model;

import java.time.Instant;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class RefreshToken {

	@SuppressWarnings("unused")
	private Instant expiresAt;

	@SuppressWarnings("unused")
	private UUID id;

	@SuppressWarnings("unused")
	private Instant issuedAt;

	@SuppressWarnings("unused")
	private boolean revoked;

	@SuppressWarnings("unused")
	private String rotationParentId;

	@SuppressWarnings("unused")
	private UUID sessionId;

	@SuppressWarnings("unused")
	private UUID tenantId;

	@SuppressWarnings("unused")
	private UUID userId;

}