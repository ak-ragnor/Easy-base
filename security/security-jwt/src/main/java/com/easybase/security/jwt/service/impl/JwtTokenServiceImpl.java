/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.jwt.service.impl;

import com.easybase.security.api.constants.SecurityConstants;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.api.dto.TokenClaims;
import com.easybase.security.api.dto.TokenValidationResult;
import com.easybase.security.api.exception.InvalidTokenException;
import com.easybase.security.api.service.TokenService;
import com.easybase.security.jwt.config.JwtProperties;
import com.easybase.security.jwt.service.KeyManager;
import com.easybase.security.session.service.RefreshTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

import java.nio.charset.StandardCharsets;

import java.security.KeyPair;
import java.security.MessageDigest;

import java.time.Duration;
import java.time.Instant;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SecretKey;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Implementation of {@link TokenService} that provides JWT token operations
 * including generation, parsing, and validation of access and refresh tokens.
 *
 * <p>This service handles both symmetric (HS256) and asymmetric (RS256/384/512)
 * signing algorithms and integrates with the refresh token storage mechanism.</p>
 *
 * @author Akhash
 */
@RequiredArgsConstructor
@Service
public class JwtTokenServiceImpl implements TokenService {

	@Override
	public String generateAccessToken(
		AuthenticatedPrincipalData principal, Duration ttl) {

		Instant now = Instant.now();

		Instant expiration = now.plus(ttl);

		JwtBuilder jwtBuilder = Jwts.builder(
		).issuer(
			_jwtProperties.getIssuer()
		).subject(
			String.valueOf(principal.getUserId())
		).audience(
		).add(
			_jwtProperties.getAudience()
		).and(
		).issuedAt(
			Date.from(now)
		).expiration(
			Date.from(expiration)
		).id(
			String.valueOf(UUID.randomUUID())
		).claim(
			SecurityConstants.JWT_CLAIM_TENANT_ID,
			String.valueOf(principal.getTenantId())
		).claim(
			SecurityConstants.JWT_CLAIM_SESSION_ID, principal.getSessionId()
		);

		List<String> authoritiesList = principal.getAuthorities();

		if ((authoritiesList != null) && !authoritiesList.isEmpty()) {
			jwtBuilder.claim(
				SecurityConstants.JWT_CLAIM_ROLES, authoritiesList);
		}

		return jwtBuilder.header(
		).keyId(
			_keyManager.getCurrentKeyId()
		).and(
		).signWith(
			_keyManager.getSigningKey(), _getSignatureAlgorithm()
		).compact();
	}

	@Override
	public String generateRefreshToken(String sessionId) {
		Instant now = Instant.now();

		Instant expiration = now.plus(_jwtProperties.getRefreshTokenTtl());

		String refreshToken = Jwts.builder(
		).issuer(
			_jwtProperties.getIssuer()
		).subject(
			SecurityConstants.REFRESH
		).audience(
		).add(
			_jwtProperties.getAudience()
		).and(
		).issuedAt(
			Date.from(now)
		).expiration(
			Date.from(expiration)
		).id(
			String.valueOf(UUID.randomUUID())
		).claim(
			SecurityConstants.JWT_CLAIM_SESSION_ID, sessionId
		).header(
		).keyId(
			_keyManager.getCurrentKeyId()
		).and(
		).signWith(
			_keyManager.getSigningKey(), _getSignatureAlgorithm()
		).compact();

		// Store refresh token in database for validation

		_refreshTokenService.storeRefreshToken(
			refreshToken, sessionId, expiration);

		return refreshToken;
	}

	@Override
	public TokenClaims parseUnverified(String token) {
		try {
			String[] chunks = token.split("\\.");

			if (chunks.length != 3) {
				throw new InvalidTokenException("Invalid JWT format");
			}

			JwtParserBuilder jwtParserBuilder = Jwts.parser();

			Claims claims = jwtParserBuilder.unsecured(
			).build(
			).parseUnsecuredClaims(
				token
			).getPayload();

			return _mapToTokenClaims(claims);
		}
		catch (Exception exception) {
			throw new InvalidTokenException(
				"Failed to parse token claims", exception);
		}
	}

	@Override
	public TokenValidationResult validateAccessToken(String token) {
		try {
			JwtParser jwtParser = _createJwtParser();

			Jws<Claims> jws = jwtParser.parseSignedClaims(token);

			Claims claims = jws.getPayload();

			TokenClaims tokenClaims = _mapToTokenClaims(claims);

			return TokenValidationResult.valid(tokenClaims);
		}
		catch (ExpiredJwtException expiredJwtException) {
			return TokenValidationResult.invalid("Token expired");
		}
		catch (UnsupportedJwtException unsupportedJwtException) {
			return TokenValidationResult.invalid("Unsupported token");
		}
		catch (MalformedJwtException malformedJwtException) {
			return TokenValidationResult.invalid("Malformed token");
		}
		catch (IllegalArgumentException illegalArgumentException) {
			return TokenValidationResult.invalid("Invalid token");
		}
		catch (Exception exception) {
			return TokenValidationResult.invalid("Token validation failed");
		}
	}

	@Override
	public boolean validateRefreshToken(String refreshToken, String sessionId) {
		try {
			JwtParser parser = _createJwtParser();

			Jws<Claims> jws = parser.parseSignedClaims(refreshToken);

			Claims claims = jws.getPayload();

			String tokenSessionId = claims.get(
				SecurityConstants.JWT_CLAIM_SESSION_ID, String.class);

			if (_constantTimeEquals(sessionId, tokenSessionId) &&
				_constantTimeEquals(
					SecurityConstants.REFRESH, claims.getSubject())) {

				return true;
			}

			return false;
		}
		catch (Exception exception) {
			return false;
		}
	}

	private boolean _constantTimeEquals(String a, String b) {
		if ((a == null) || (b == null)) {
			return false;
		}

		byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
		byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);

		return MessageDigest.isEqual(aBytes, bBytes);
	}

	private JwtParser _createJwtParser() {
		JwtParserBuilder builder = Jwts.parser();

		if (Objects.equals("HS256", _jwtProperties.getAlgorithm())) {
			builder.verifyWith((SecretKey)_keyManager.getSigningKey());
		}
		else {
			KeyPair currentKeyPair = _keyManager.getCurrentKeyPair();

			builder.verifyWith(currentKeyPair.getPublic());
		}

		return builder.requireIssuer(
			_jwtProperties.getIssuer()
		).requireAudience(
			_jwtProperties.getAudience()
		).build();
	}

	private SignatureAlgorithm _getSignatureAlgorithm() {
		switch (_jwtProperties.getAlgorithm()) {
			case "HS256":
				return SignatureAlgorithm.HS256;
			case "RS256":
				return SignatureAlgorithm.RS256;
			case "RS384":
				return SignatureAlgorithm.RS384;
			case "RS512":
				return SignatureAlgorithm.RS512;
			default:
				throw new IllegalArgumentException(
					"Unsupported algorithm: " + _jwtProperties.getAlgorithm());
		}
	}

	private TokenClaims _mapToTokenClaims(Claims claims) {
		String userIdStr = claims.getSubject();
		String tenantIdStr = claims.get(
			SecurityConstants.JWT_CLAIM_TENANT_ID, String.class);
		String sessionId = claims.get(
			SecurityConstants.JWT_CLAIM_SESSION_ID, String.class);
		List<String> roles = claims.get(
			SecurityConstants.JWT_CLAIM_ROLES, List.class);

		Set<String> audience = claims.getAudience();

		Iterator<String> audienceIterator = audience.iterator();

		Date issuedAt = claims.getIssuedAt();
		Date expiresAt = claims.getExpiration();

		TokenClaims tokenClaims = new TokenClaims();

		tokenClaims.setIssuer(claims.getIssuer());
		tokenClaims.setUserId(UUID.fromString(userIdStr));
		tokenClaims.setTenantId(UUID.fromString(tenantIdStr));
		tokenClaims.setSessionId(sessionId);
		tokenClaims.setAudience(audienceIterator.next());
		tokenClaims.setIssuedAt(issuedAt.toInstant());
		tokenClaims.setExpiresAt(expiresAt.toInstant());
		tokenClaims.setTokenId(claims.getId());
		tokenClaims.setRoles(roles);

		return tokenClaims;
	}

	private final JwtProperties _jwtProperties;
	private final KeyManager _keyManager;
	private final RefreshTokenService _refreshTokenService;

}