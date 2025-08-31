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