/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.session.repository;

import com.easybase.security.session.entity.RefreshTokenEntity;

import java.time.Instant;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Akhash
 */
@Repository
public interface RefreshTokenRepository
	extends JpaRepository<RefreshTokenEntity, Long> {

	@Modifying
	@Query(
		"DELETE FROM RefreshTokenEntity rt WHERE rt.expiresAt < :expiredBefore OR (rt.revoked = true AND rt.revokedAt < :revokedBefore)"
	)
	public void deleteExpiredAndOldRevokedTokens(
		@Param("expiredBefore") Instant expiredBefore,
		@Param("revokedBefore") Instant revokedBefore);

	public Optional<RefreshTokenEntity> findByTokenHashAndRevokedFalse(
		String tokenHash);

	@Modifying
	@Query(
		"UPDATE RefreshTokenEntity rt SET rt.revoked = true, rt.revokedAt = :revokedAt WHERE rt.sessionId = :sessionId"
	)
	public void revokeBySessionId(
		@Param("sessionId") String sessionId,
		@Param("revokedAt") Instant revokedAt);

	@Modifying
	@Query(
		"UPDATE RefreshTokenEntity rt SET rt.revoked = true, rt.revokedAt = :revokedAt WHERE rt.tokenHash = :tokenHash"
	)
	public void revokeByTokenHash(
		@Param("tokenHash") String tokenHash,
		@Param("revokedAt") Instant revokedAt);

}