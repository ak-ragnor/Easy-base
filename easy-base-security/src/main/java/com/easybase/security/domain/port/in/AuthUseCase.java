/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.domain.port.in;

import com.easybase.security.dto.TokenResponse;

import java.util.UUID;

/**
 * @author Akhash R
 */
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