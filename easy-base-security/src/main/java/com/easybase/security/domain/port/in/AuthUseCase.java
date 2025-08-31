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

package com.easybase.security.domain.port.in;

import com.easybase.security.dto.TokenResponse;

import java.util.UUID;

public interface AuthUseCase {

	public UUID getCurrentTenantId(String sessionToken);

	public UUID getCurrentUserId(String sessionToken);

	public TokenResponse login(
		UUID tenantId, String email, String password, String userAgent,
		String ipAddress);

	public TokenResponse refresh(
		String refreshToken, String userAgent, String ipAddress);

	public void revoke(String sessionToken);

	public void revokeAll(UUID userId, UUID tenantId);

	public boolean validateToken(String sessionToken);

}