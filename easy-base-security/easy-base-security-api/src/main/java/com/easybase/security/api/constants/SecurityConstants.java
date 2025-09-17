/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.constants;

/**
 * @author Akhash
 */
public final class SecurityConstants {

	public static final String ANONYMOUS_SESSION_ID = "anonymous";

	public static final String AUTHORIZATION_HEADER = "Authorization";

	public static final String BEARER_PREFIX = "Bearer ";

	public static final String JWT_AUDIENCE = "easy-base-platform";

	public static final String JWT_CLAIM_ROLES = "roles";

	public static final String JWT_CLAIM_SESSION_ID = "sid";

	public static final String JWT_CLAIM_TENANT_ID = "tid";

	public static final String JWT_CLAIM_TOKEN_ID = "jti";

	public static final String JWT_CLAIM_USER_ID = "sub";

	public static final String JWT_ISSUER = "easy-base-auth";

	private SecurityConstants() {
	}

}