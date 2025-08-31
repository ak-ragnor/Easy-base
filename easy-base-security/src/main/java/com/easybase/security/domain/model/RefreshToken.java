/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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