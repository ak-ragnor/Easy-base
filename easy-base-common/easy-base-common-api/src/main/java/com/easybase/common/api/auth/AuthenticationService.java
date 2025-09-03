/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.api.auth;

import com.easybase.common.exception.ExpiredTokenException;
import com.easybase.common.exception.InvalidTokenException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service interface for authenticating HTTP requests.
 * Implementations can use different authentication strategies.
 *
 * @author Akhash R
 */
public interface AuthenticationService {

	/**
	 * Authenticates the HTTP request and returns authentication result.
	 *
	 * @param request the HTTP request to authenticate
	 * @return authentication result containing user and tenant information
	 * @throws InvalidTokenException if token is invalid or missing
	 * @throws ExpiredTokenException if token has expired
	 */
	AuthResult _authenticate(HttpServletRequest request)
		throws ExpiredTokenException, InvalidTokenException;

}