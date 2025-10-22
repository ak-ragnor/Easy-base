/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.service;

import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.api.dto.LoginRequest;

/**
 * @author Akhash
 */
public interface AuthenticationFacade {

	public AuthenticatedPrincipalData authenticateByToken(String token);

	public AuthenticatedPrincipalData authenticateCredentials(
		LoginRequest loginRequest);

	public void logout(String sessionId);

	public AuthenticatedPrincipalData refreshAuthentication(
		String refreshToken);

	public void revokeRefreshToken(String refreshToken);

}