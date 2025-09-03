/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.adapter.in.auth;

import com.easybase.common.api.auth.AuthResult;
import com.easybase.common.api.auth.TokenValidator;
import com.easybase.common.exception.ExpiredTokenException;
import com.easybase.common.exception.InvalidTokenException;
import com.easybase.security.domain.port.in.AuthUseCase;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * JWT-based token validator implementation.
 * Handles Bearer token authentication by validating JWT tokens.
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenValidator implements TokenValidator {

	@Override
	public boolean supports(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if ((authHeader != null) && authHeader.startsWith("Bearer ")) {
			return true;
		}

		return false;
	}

	@Override
	public AuthResult validate(HttpServletRequest request, String token)
		throws ExpiredTokenException, InvalidTokenException {

		try {
			boolean valid = _authUseCase.validateToken(token);

			if (!valid) {
				throw new InvalidTokenException("Token validation failed");
			}

			UUID userId = _authUseCase.getCurrentUserId(token);
			UUID tenantId = _authUseCase.getCurrentTenantId(token);

			return new AuthResult(
				userId, tenantId, new String[0], new String[0], null, null);
		}
		catch (Exception exception) {
			log.debug(
				"JWT token validation failed: {}", exception.getMessage());

			if (_isExpiredTokenException(exception)) {
				throw new ExpiredTokenException("Token has expired", exception);
			}

			throw new InvalidTokenException("Invalid JWT token", exception);
		}
	}

	private boolean _isExpiredTokenException(Exception exception) {
		String message = exception.getMessage();

		if ((message != null) &&
			(message.contains("expired") || message.contains("Expired"))) {

			return true;
		}

		return false;
	}

	private final AuthUseCase _authUseCase;

}