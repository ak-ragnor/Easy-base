/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.security.domain.service;

import com.easybase.common.exception.InvalidRequestException;
import com.easybase.security.domain.model.AuthSession;
import com.easybase.security.domain.model.RefreshToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthDomainService {

	public RefreshToken createRefreshToken(
		UUID userId, UUID tenantId, UUID sessionId, String rotationParentId) {

		Instant now = Instant.now();

		return RefreshToken.builder(
		).id(
			UUID.randomUUID()
		).userId(
			userId
		).tenantId(
			tenantId
		).sessionId(
			sessionId
		).issuedAt(
			now
		).expiresAt(
			now.plus(_refreshTokenExpirationDays, ChronoUnit.DAYS)
		).revoked(
			false
		).rotationParentId(
			rotationParentId
		).build();
	}

	public AuthSession createSession(
		UUID userId, UUID tenantId, String sessionToken, String userAgent,
		String ipAddress) {

		Instant now = Instant.now();

		return AuthSession.builder(
		).id(
			UUID.randomUUID()
		).userId(
			userId
		).tenantId(
			tenantId
		).sessionToken(
			sessionToken
		).expiresAt(
			now.plus(_sessionExpirationDays, ChronoUnit.DAYS)
		).userAgent(
			userAgent
		).ipAddress(
			ipAddress
		).revoked(
			false
		).createdAt(
			now
		).updatedAt(
			now
		).build();
	}

	public AuthSession extendSession(AuthSession session) {
		if (session == null) {
			throw new InvalidRequestException("Session cannot be null");
		}

		Instant now = Instant.now();

		AuthSession.AuthSessionBuilder builder = session.toBuilder();

		builder.expiresAt(now.plus(_sessionExpirationDays, ChronoUnit.DAYS));
		builder.updatedAt(now);

		return builder.build();
	}

	public boolean isRefreshTokenValid(RefreshToken refreshToken) {
		if (refreshToken == null) {
			return false;
		}

		Instant now = Instant.now();
		Instant expiresAt = refreshToken.getExpiresAt();

		if (!refreshToken.isRevoked() && expiresAt.isAfter(now)) {
			return true;
		}

		return false;
	}

	public boolean isSessionValid(AuthSession session) {
		if (session == null) {
			return false;
		}

		Instant now = Instant.now();
		Instant expiresAt = session.getExpiresAt();

		if (!session.isRevoked() && expiresAt.isAfter(now)) {
			return true;
		}

		return false;
	}

	public AuthSession refreshSession(
		AuthSession existingSession, String newSessionToken) {

		Instant now = Instant.now();

		AuthSession.AuthSessionBuilder builder = existingSession.toBuilder();

		builder.sessionToken(newSessionToken);
		builder.expiresAt(now.plus(_sessionExpirationDays, ChronoUnit.DAYS));
		builder.updatedAt(now);

		return builder.build();
	}

	public RefreshToken revokeRefreshToken(RefreshToken refreshToken) {
		if (refreshToken == null) {
			throw new InvalidRequestException("Refresh token cannot be null");
		}

		RefreshToken.RefreshTokenBuilder builder = refreshToken.toBuilder();

		builder.revoked(true);

		return builder.build();
	}

	public AuthSession revokeSession(AuthSession session) {
		if (session == null) {
			throw new InvalidRequestException("Session cannot be null");
		}

		AuthSession.AuthSessionBuilder builder = session.toBuilder();

		builder.revoked(true);
		builder.updatedAt(Instant.now());

		return builder.build();
	}

	@Value("${easy-base.security.refresh-token.expiration-days:14}")
	private long _refreshTokenExpirationDays;

	@Value("${easy-base.security.session.expiration-days:14}")
	private long _sessionExpirationDays;

}