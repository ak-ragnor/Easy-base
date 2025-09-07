/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.adapter.in.auth;

import com.easybase.common.exception.ExpiredTokenException;
import com.easybase.common.exception.InvalidTokenException;
import com.easybase.security.domain.model.AuthResult;
import com.easybase.security.domain.port.in.AuthenticationService;
import com.easybase.security.domain.port.in.TokenValidator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * Facade that dynamically selects appropriate token validator
 * based on the incoming HTTP request characteristics.
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFacade implements AuthenticationService {

	/**
	 * Authenticates the request by finding and using the appropriate validator.
	 *
	 * @param request the HTTP request to authenticate
	 * @return authentication result
	 * @throws InvalidTokenException if no validator supports the request or validation fails
	 * @throws ExpiredTokenException if token is expired
	 */
	@Override
	public AuthResult authenticate(HttpServletRequest request)
		throws ExpiredTokenException, InvalidTokenException {

		for (TokenValidator validator : _validators) {
			if (validator.supports(request)) {
				String token = _extractToken(request);

				if (token == null) {
					throw new InvalidTokenException(
						"No token found in supported request");
				}

				log.debug(
					"Using validator: {} for request", validator.getClass());

				return validator.validate(request, token);
			}
		}

		throw new InvalidTokenException(
			"No authentication strategy matched request");
	}

	private String _extractToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null) {
			if (authHeader.startsWith("Bearer ")) {
				return authHeader.substring(7);
			}

			if (authHeader.startsWith("Basic ")) {
				return authHeader.substring(6);
			}
		}

		String apiKey = request.getHeader("X-Api-Key");

		if (apiKey != null) {
			return apiKey;
		}

		return null;
	}

	private final List<TokenValidator> _validators;

}