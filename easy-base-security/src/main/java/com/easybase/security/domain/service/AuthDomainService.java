package com.easybase.security.domain.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.easybase.security.domain.model.AuthSession;
import com.easybase.security.domain.model.RefreshToken;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthDomainService {

	public AuthSession createSession(UUID userId, UUID tenantId,
			String sessionToken, String userAgent, String ipAddress) {
		Instant now = Instant.now();

		AuthSession session = AuthSession.builder().id(UUID.randomUUID())
				.userId(userId).tenantId(tenantId).sessionToken(sessionToken)
				.expiresAt(now.plus(_JWT_EXPIRATION_HOURS, ChronoUnit.HOURS))
				.userAgent(userAgent).ipAddress(ipAddress).revoked(false)
				.createdAt(now).updatedAt(now).build();

		log.debug("Created auth session for userId={} tenantId={}", userId,
				tenantId);
		return session;
	}

	public AuthSession refreshSession(AuthSession existingSession,
			String newSessionToken) {
		Instant now = Instant.now();

		return existingSession.toBuilder().sessionToken(newSessionToken)
				.expiresAt(now.plus(_JWT_EXPIRATION_HOURS, ChronoUnit.HOURS))
				.updatedAt(now).build();
	}

	public AuthSession revokeSession(AuthSession session) {
		return session.toBuilder().revoked(true).updatedAt(Instant.now())
				.build();
	}

	public boolean isSessionValid(AuthSession session) {
		Instant now = Instant.now();
		return !session.isRevoked() && session.getExpiresAt().isAfter(now);
	}

	public RefreshToken createRefreshToken(UUID userId, UUID tenantId,
			UUID sessionId, String rotationParentId) {
		Instant now = Instant.now();

		RefreshToken refreshToken = RefreshToken.builder().id(UUID.randomUUID())
				.userId(userId).tenantId(tenantId).sessionId(sessionId)
				.issuedAt(now)
				.expiresAt(now.plus(_REFRESH_TOKEN_EXPIRATION_DAYS,
						ChronoUnit.DAYS))
				.revoked(false).rotationParentId(rotationParentId).build();

		log.debug("Created refresh token for sessionId={}", sessionId);
		return refreshToken;
	}

	public RefreshToken revokeRefreshToken(RefreshToken refreshToken) {
		return refreshToken.toBuilder().revoked(true).build();
	}

	public boolean isRefreshTokenValid(RefreshToken refreshToken) {
		Instant now = Instant.now();
		return !refreshToken.isRevoked()
				&& refreshToken.getExpiresAt().isAfter(now);
	}

	private static final long _JWT_EXPIRATION_HOURS = 1;

	private static final long _REFRESH_TOKEN_EXPIRATION_DAYS = 30;
}
