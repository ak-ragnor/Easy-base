/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.session.service;

import com.easybase.security.session.config.SessionProperties;
import com.easybase.security.session.entity.RefreshTokenEntity;
import com.easybase.security.session.repository.RefreshTokenRepository;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.time.Instant;

import java.util.Base64;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Akhash
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshTokenService {

	@Transactional
	public void cleanupExpiredTokens() {
		Instant now = Instant.now();

		Instant revokedBefore = now.minus(
			_sessionProperties.getCleanupGracePeriod());

		_refreshTokenRepository.deleteExpiredAndOldRevokedTokens(
			now, revokedBefore);

		log.debug("Cleaned up expired and old revoked refresh tokens");
	}

	@Transactional
	public void revokeRefreshToken(String refreshToken) {
		String tokenHash = _hashToken(refreshToken);

		_refreshTokenRepository.revokeByTokenHash(tokenHash, Instant.now());
	}

	@Transactional
	public void revokeRefreshTokensForSession(String sessionId) {
		_refreshTokenRepository.revokeBySessionId(sessionId, Instant.now());
	}

	@Transactional
	public void storeRefreshToken(
		String refreshToken, String sessionId, Instant expiresAt) {

		String tokenHash = _hashToken(refreshToken);

		RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();

		refreshTokenEntity.setTokenHash(tokenHash);
		refreshTokenEntity.setSessionId(sessionId);
		refreshTokenEntity.setExpiresAt(expiresAt);

		_refreshTokenRepository.save(refreshTokenEntity);
	}

	@Transactional(readOnly = true)
	public Optional<RefreshTokenEntity> validateRefreshToken(
		String refreshToken) {

		String tokenHash = _hashToken(refreshToken);

		return _refreshTokenRepository.findByTokenHashAndRevokedFalse(
			tokenHash
		).filter(
			token -> {
				Instant expiresAt = token.getExpiresAt();
				Instant now = Instant.now();

				return expiresAt.isAfter(now);
			}
		);
	}

	private String _hashToken(String token) {
		try {
			byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);

			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hash = digest.digest(tokenBytes);

			Base64.Encoder encoder = Base64.getEncoder();

			return encoder.encodeToString(hash);
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RuntimeException(
				"SHA-256 algorithm not available", noSuchAlgorithmException);
		}
	}

	private final RefreshTokenRepository _refreshTokenRepository;
	private final SessionProperties _sessionProperties;

}