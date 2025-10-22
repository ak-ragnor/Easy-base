/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.service;

import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.api.dto.TokenClaims;
import com.easybase.security.api.dto.TokenValidationResult;

import java.time.Duration;

/**
 * @author Akhash
 */
public interface TokenService {

	public String generateAccessToken(
		AuthenticatedPrincipalData principal, Duration ttl);

	public String generateRefreshToken(String sessionId);

	public TokenClaims parseUnverified(String token);

	public TokenValidationResult validateAccessToken(String token);

	public boolean validateRefreshToken(String refreshToken, String sessionId);

}