/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.persistence.repository;

import com.easybase.security.persistence.entity.AuthSessionEntity;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthSessionRepository
	extends JpaRepository<AuthSessionEntity, UUID> {

	@Modifying
	@Query("DELETE FROM AuthSessionEntity s WHERE s.expiresAt < :cutoff")
	public void deleteExpiredSessions(@Param("cutoff") Instant cutoff);

	@Query(
		"SELECT s FROM AuthSessionEntity s WHERE s.userId = :userId AND s.revoked = false AND s.expiresAt > :now ORDER BY s.updatedAt DESC"
	)
	public List<AuthSessionEntity> findActiveByUserId(
		@Param("userId") UUID userId, @Param("now") Instant now);

	@Query(
		"SELECT s FROM AuthSessionEntity s WHERE s.userId = :userId AND s.tenantId = :tenantId AND s.revoked = false AND s.expiresAt > :now ORDER BY s.updatedAt DESC"
	)
	public List<AuthSessionEntity> findActiveByUserIdAndTenantId(
		@Param("userId") UUID userId, @Param("tenantId") UUID tenantId,
		@Param("now") Instant now);

	public Optional<AuthSessionEntity> findBySessionToken(String sessionToken);

	@Modifying
	@Query(
		"UPDATE AuthSessionEntity s SET s.revoked = true WHERE s.userId = :userId"
	)
	public void revokeAllByUserId(@Param("userId") UUID userId);

	@Modifying
	@Query(
		"UPDATE AuthSessionEntity s SET s.revoked = true WHERE s.userId = :userId AND s.tenantId = :tenantId"
	)
	public void revokeAllByUserIdAndTenantId(
		@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

}