/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.jwt.config;

import java.time.Duration;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Akhash
 */
@ConfigurationProperties(prefix = "easy.base.security.jwt")
@Data
public class JwtProperties {

	@SuppressWarnings("unused")
	private Duration accessTokenTtl = Duration.ofMinutes(15);

	@SuppressWarnings("unused")
	private String algorithm = "RS256";

	@SuppressWarnings("unused")
	private String audience = "easy-base-platform";

	@SuppressWarnings("unused")
	private String issuer = "easy-base-auth";

	@SuppressWarnings("unused")
	private String keyAlias;

	@SuppressWarnings("unused")
	private String keyId = "default";

	@SuppressWarnings("unused")
	private String keyStoreLocation;

	@SuppressWarnings("unused")
	private String keyStorePassword;

	@SuppressWarnings("unused")
	private String privateKey;

	@SuppressWarnings("unused")
	private String publicKey;

	@SuppressWarnings("unused")
	private Duration refreshTokenTtl = Duration.ofDays(7);

	@SuppressWarnings("unused")
	private boolean rotateRefreshTokens = true;

	@SuppressWarnings("unused")
	private String secretKey;

}