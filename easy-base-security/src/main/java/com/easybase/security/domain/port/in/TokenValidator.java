/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.domain.port.in;

import com.easybase.common.exception.ExpiredTokenException;
import com.easybase.common.exception.InvalidTokenException;
import com.easybase.security.domain.model.AuthResult;

import jakarta.servlet.http.HttpServletRequest;

/**
 * SPI interface for token validation strategies.
 * Implementations can provide different authentication mechanisms
 * such as JWT, Basic Auth, API Keys, etc.
 *
 * @author Akhash R
 */
public interface TokenValidator {

	/**
	 * Checks if this validator supports the given HTTP request.
	 * Implementations should examine headers or other request properties
	 * to determine if they can handle the authentication.
	 *
	 * @param request the HTTP request to examine
	 * @return true if this validator can handle the request
	 */
	public boolean supports(HttpServletRequest request);

	/**
	 * Validates the token and extracts authentication information.
	 *
	 * @param request the HTTP request containing the token
	 * @param token   the extracted token string
	 * @return authentication result with user/tenant information
	 * @throws InvalidTokenException if token format or signature is invalid
	 * @throws ExpiredTokenException if token is expired
	 */
	public AuthResult validate(HttpServletRequest request, String token)
		throws ExpiredTokenException, InvalidTokenException;

}