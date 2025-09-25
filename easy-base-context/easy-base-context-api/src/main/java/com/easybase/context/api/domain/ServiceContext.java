/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.domain;

import com.easybase.context.api.constants.ServiceContextConstants;

import java.time.Instant;

import java.util.Optional;
import java.util.UUID;

/**
 * Service context interface providing access to current user and tenant information
 * within the scope of the current request/session. Uses lightweight DTOs instead of
 * heavy JPA entities for better performance and cleaner architecture.
 *
 * @author Akhash R
 */
public interface ServiceContext {

	/**
	 * Gets the client IP address for the current request.
	 *
	 * @return client IP address, empty if unavailable
	 */
	public Optional<String> clientIp();

	/**
	 * Gets correlation IDs for request tracing.
	 *
	 * @return correlation IDs for distributed tracing
	 */
	public CorrelationIds correlation();

	/**
	 * Gets when the authentication token expires.
	 *
	 * @return token expiration timestamp, may be null for anonymous access
	 */
	public Instant expiresAt();


	/**
	 * Checks if the current context represents an authenticated user.
	 *
	 * @return true if user is authenticated (not anonymous)
	 */
	public default boolean isAuthenticated() {
		return !ServiceContextConstants.ANONYMOUS_ID.equals(user().id());
	}

	/**
	 * Gets when the authentication token was issued.
	 *
	 * @return token issued timestamp, may be null for anonymous access
	 */
	public Instant issuedAt();


	/**
	 * Gets the current tenant information.
	 *
	 * @return the current tenant info with lazy loading
	 */
	public TenantInfo tenant();

	/**
	 * Gets the current tenant ID directly.
	 * Convenience method to avoid object creation when only ID is needed.
	 *
	 * @return the current tenant ID
	 */
	public default UUID tenantId() {
		return tenant().id();
	}

	/**
	 * Gets the current user information.
	 *
	 * @return the current user info with lazy loading
	 */
	public UserInfo user();

	/**
	 * Gets the user agent for the current request.
	 *
	 * @return user agent string, empty if unavailable
	 */
	public Optional<String> userAgent();

	/**
	 * Gets the current user ID directly.
	 * Convenience method to avoid object creation when only ID is needed.
	 *
	 * @return the current user ID
	 */
	public default UUID userId() {
		return user().id();
	}

}