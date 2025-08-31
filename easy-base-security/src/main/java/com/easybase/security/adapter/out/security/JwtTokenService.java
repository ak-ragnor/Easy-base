/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.adapter.out.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtTokenService {

	public String generateAccessToken(
		UUID userId, UUID tenantId, String email) {

		String sanitizedEmail = email.trim();

		return Jwts.builder(
		).subject(
			userId.toString()
		).claim(
			"tenantId", tenantId.toString()
		).claim(
			"email", sanitizedEmail.toLowerCase()
		).claim(
			"type", "access"
		).issuedAt(
			new Date()
		).expiration(
			_getExpiry(_jwtExpirationSeconds)
		).signWith(
			_getSigningKey(), Jwts.SIG.HS256
		).compact();
	}

	public Claims getClaimsFromToken(String token) {
		if (token == null) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}

		String trimmedToken = token.trim();

		if (trimmedToken.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}

		try {
			JwtParserBuilder parserBuilder = Jwts.parser();

			parserBuilder = parserBuilder.verifyWith(_getSigningKey());

			JwtParser parser = parserBuilder.build();

			Jws<Claims> jws = parser.parseSignedClaims(trimmedToken);

			return jws.getPayload();
		}
		catch (IllegalArgumentException | JwtException exception) {
			log.warn("Failed to parse JWT claims: {}", exception.getMessage());

			throw exception;
		}
	}

	public String getEmailFromToken(String token) {
		Claims claims = getClaimsFromToken(token);

		return claims.get("email", String.class);
	}

	public UUID getTenantIdFromToken(String token) {
		Claims claims = getClaimsFromToken(token);

		String tenantIdString = claims.get("tenantId", String.class);

		return UUID.fromString(tenantIdString);
	}

	public UUID getUserIdFromToken(String token) {
		Claims claims = getClaimsFromToken(token);

		String userIdString = claims.getSubject();

		return UUID.fromString(userIdString);
	}

	public boolean isAccessToken(String token) {
		Claims claims = getClaimsFromToken(token);

		String type = claims.get("type", String.class);

		return Objects.equals(type, "access");
	}

	public boolean isTokenExpired(String token) {
		try {
			Claims claims = getClaimsFromToken(token);

			Date expiration = claims.getExpiration();

			return expiration.before(new Date());
		}
		catch (ExpiredJwtException expiredJwtException) {
			return true;
		}
		catch (JwtException jwtException) {
			log.debug(
				"Invalid token while checking expiration: {}",
				jwtException.getMessage());

			return true;
		}
	}

	public boolean validateToken(String token) {
		if (token == null) {
			return false;
		}

		String trimmedToken = token.trim();

		if (trimmedToken.isEmpty()) {
			return false;
		}

		try {
			JwtParserBuilder parserBuilder = Jwts.parser();

			parserBuilder = parserBuilder.verifyWith(_getSigningKey());

			JwtParser parser = parserBuilder.build();

			parser.parseSignedClaims(trimmedToken);

			return true;
		}
		catch (IllegalArgumentException | JwtException exception) {
			log.debug("JWT validation failed: {}", exception.getMessage());

			return false;
		}
	}

	private Date _getExpiry(long seconds) {
		long millis = seconds * 1000;
		long now = System.currentTimeMillis();

		return new Date(now + millis);
	}

	private SecretKey _getSigningKey() {
		byte[] secretBytes = _jwtSecret.getBytes(StandardCharsets.UTF_8);

		return Keys.hmacShaKeyFor(secretBytes);
	}

	@Value("${easy-base.security.jwt.expiration:900}")
	private long _jwtExpirationSeconds;

	@Value("${easy-base.security.jwt.secret:mysecretmysecretmysecretmysecret}")
	private String _jwtSecret;

}