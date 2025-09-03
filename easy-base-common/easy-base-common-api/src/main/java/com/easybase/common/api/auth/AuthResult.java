/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.api.auth;

import com.easybase.common.api.context.ServiceContextConstants;

import java.time.Instant;

import java.util.UUID;

/**
 * Immutable class representing the result of token authentication.
 * Contains the essential information extracted from a validated token.
 *
 * @author Akhash R
 */
public class AuthResult {

	/**
	 * Creates an anonymous auth result for unauthenticated requests.
	 *
	 * @return anonymous auth result
	 */
	public static AuthResult anonymous() {
		return new AuthResult(
			ServiceContextConstants.ANONYMOUS_ID,
			ServiceContextConstants.PUBLIC_TENANT_ID, new String[0],
			new String[0], null, null);
	}

	public AuthResult(
		UUID userId, UUID tenantId, String[] roles, String[] scopes,
		Instant issuedAt, Instant expiresAt) {

		_userId = userId;
		_tenantId = tenantId;
		_roles = roles;
		_scopes = scopes;
		_issuedAt = issuedAt;
		_expiresAt = expiresAt;
	}

	public Instant getExpiresAt() {
		return _expiresAt;
	}

	public Instant getIssuedAt() {
		return _issuedAt;
	}

	public String[] getRoles() {
		return _roles;
	}

	public String[] getScopes() {
		return _scopes;
	}

	public UUID getTenantId() {
		return _tenantId;
	}

	public UUID getUserId() {
		return _userId;
	}

	/**
	 * Checks if this auth result represents an authenticated user.
	 *
	 * @return true if user is authenticated (not anonymous)
	 */
	public boolean isAuthenticated() {
		return !ServiceContextConstants.ANONYMOUS_ID.equals(_userId);
	}

	/**
	 * Checks if the token is currently valid (not expired).
	 *
	 * @return true if token is not expired
	 */
	public boolean isValid() {
		if (_expiresAt == null) {
			return true;
		}

		Instant now = Instant.now();

		return now.isBefore(_expiresAt);
	}

	private final Instant _expiresAt;
	private final Instant _issuedAt;
	private final String[] _roles;
	private final String[] _scopes;
	private final UUID _tenantId;
	private final UUID _userId;

}