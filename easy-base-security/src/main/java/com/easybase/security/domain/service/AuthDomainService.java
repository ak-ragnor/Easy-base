package com.easybase.security.domain.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.easybase.common.exception.InvalidRequestException;
import com.easybase.security.domain.model.AuthSession;
import com.easybase.security.domain.model.RefreshToken;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthDomainService {

	public AuthSession createSession(UUID userId, UUID tenantId,
			String sessionToken, String userAgent, String ipAddress) {

		Instant now = Instant.now();

		return AuthSession.builder().id(UUID.randomUUID()).userId(userId)
				.tenantId(tenantId).sessionToken(sessionToken)
				.expiresAt(now.plus(sessionExpirationDays, ChronoUnit.DAYS))
				.userAgent(userAgent).ipAddress(ipAddress).revoked(false)
				.createdAt(now).updatedAt(now).build();
	}

	public AuthSession refreshSession(AuthSession existingSession,
			String newSessionToken) {
		Instant now = Instant.now();

		return existingSession.toBuilder().sessionToken(newSessionToken)
				.expiresAt(now.plus(sessionExpirationDays, ChronoUnit.DAYS))
				.updatedAt(now).build();
	}

	public AuthSession revokeSession(AuthSession session) {
		if (session == null) {
			throw new InvalidRequestException("Session cannot be null");
		}
		return session.toBuilder().revoked(true).updatedAt(Instant.now())
				.build();
	}

	public boolean isSessionValid(AuthSession session) {
		if (session == null) {
			return false;
		}

		Instant now = Instant.now();

		return !session.isRevoked() && session.getExpiresAt().isAfter(now);
	}

	public AuthSession extendSession(AuthSession session) {
		if (session == null) {
			throw new InvalidRequestException("Session cannot be null");
		}

		Instant now = Instant.now();

		return session.toBuilder()
				.expiresAt(now.plus(sessionExpirationDays, ChronoUnit.DAYS))
				.updatedAt(now).build();
	}

	public RefreshToken createRefreshToken(UUID userId, UUID tenantId,
			UUID sessionId, String rotationParentId) {
		Instant now = Instant.now();

		RefreshToken refreshToken = RefreshToken.builder().id(UUID.randomUUID())
				.userId(userId).tenantId(tenantId).sessionId(sessionId)
				.issuedAt(now)
				.expiresAt(
						now.plus(refreshTokenExpirationDays, ChronoUnit.DAYS))
				.revoked(false).rotationParentId(rotationParentId).build();

		log.debug("Created refresh token for sessionId={}", sessionId);

		return refreshToken;
	}

	public RefreshToken revokeRefreshToken(RefreshToken refreshToken) {
		if (refreshToken == null) {
			throw new InvalidRequestException("Refresh token cannot be null");
		}

		return refreshToken.toBuilder().revoked(true).build();
	}

	public boolean isRefreshTokenValid(RefreshToken refreshToken) {
		if (refreshToken == null) {
			return false;
		}

		Instant now = Instant.now();

		return !refreshToken.isRevoked()
				&& refreshToken.getExpiresAt().isAfter(now);
	}

	@Value("${easy-base.security.session.expiration-days:14}")
	private long sessionExpirationDays;

	@Value("${easy-base.security.refresh-token.expiration-days:14}")
	private long refreshTokenExpirationDays;
}
