/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.session.repository;

import com.easybase.security.session.entity.SessionEntity;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Akhash
 */
@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

	@Query(
		"SELECT COUNT(s) FROM SessionEntity s WHERE s.userId = :userId AND s.tenantId = :tenantId AND s.revoked = false AND s.expiresAt > :now"
	)
	public long countActiveSessionsByUser(
		@Param("userId") UUID userId, @Param("tenantId") UUID tenantId,
		@Param("now") Instant now);

	@Modifying
	@Query(
		"DELETE FROM SessionEntity s WHERE s.expiresAt < :expiredBefore OR (s.revoked = true AND s.revokedAt < :revokedBefore)"
	)
	public void deleteExpiredAndOldRevokedSessions(
		@Param("expiredBefore") Instant expiredBefore,
		@Param("revokedBefore") Instant revokedBefore);

	public Optional<SessionEntity> findBySessionIdAndRevokedFalse(
		String sessionId);

	public List<SessionEntity>
		findByUserIdAndTenantIdAndRevokedFalseOrderByCreatedAtDesc(
			UUID userId, UUID tenantId);

	@Modifying
	@Query(
		"UPDATE SessionEntity s SET s.revoked = true, s.revokedAt = :revokedAt WHERE s.userId = :userId AND s.tenantId = :tenantId AND s.revoked = false"
	)
	public void revokeAllByUserIdAndTenantId(
		@Param("userId") UUID userId, @Param("tenantId") UUID tenantId,
		@Param("revokedAt") Instant revokedAt);

	@Modifying
	@Query(
		"UPDATE SessionEntity s SET s.revoked = true, s.revokedAt = :revokedAt WHERE s.sessionId = :sessionId"
	)
	public void revokeBySessionId(
		@Param("sessionId") String sessionId,
		@Param("revokedAt") Instant revokedAt);

	@Modifying
	@Query(
		"UPDATE SessionEntity s SET s.lastAccessedAt = :accessTime WHERE s.sessionId = :sessionId"
	)
	public void updateLastAccessedTime(
		@Param("sessionId") String sessionId,
		@Param("accessTime") Instant accessTime);

	@Modifying
	@Query(
		"UPDATE SessionEntity s SET s.expiresAt = :expiresAt WHERE s.sessionId = :sessionId"
	)
	public void updateSessionExpiration(
		@Param("sessionId") String sessionId,
		@Param("expiresAt") Instant expiresAt);

}