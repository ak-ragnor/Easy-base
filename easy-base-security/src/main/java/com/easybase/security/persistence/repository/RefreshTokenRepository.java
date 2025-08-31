/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.persistence.repository;

import com.easybase.security.persistence.entity.RefreshTokenEntity;

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
public interface RefreshTokenRepository
	extends JpaRepository<RefreshTokenEntity, UUID> {

	@Modifying
	@Query("DELETE FROM RefreshTokenEntity rt WHERE rt.expiresAt < :now")
	public void deleteExpiredTokens(@Param("now") Instant now);

	@Query(
		"SELECT rt FROM RefreshTokenEntity rt WHERE rt.sessionId = :sessionId AND rt.revoked = false"
	)
	public Optional<RefreshTokenEntity> findActiveBySessionId(
		@Param("sessionId") UUID sessionId);

	@Query(
		"SELECT rt FROM RefreshTokenEntity rt WHERE rt.userId = :userId AND rt.tenantId = :tenantId AND rt.revoked = false"
	)
	public List<RefreshTokenEntity> findActiveByUserIdAndTenantId(
		@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

	public Optional<RefreshTokenEntity> findByIdAndRevokedFalse(UUID tokenId);

	@Query(
		"SELECT rt FROM RefreshTokenEntity rt WHERE rt.rotationParentId = :parentId AND rt.revoked = false"
	)
	public List<RefreshTokenEntity> findByRotationParentId(
		@Param("parentId") String parentId);

	@Modifying
	@Query(
		"UPDATE RefreshTokenEntity rt SET rt.revoked = true WHERE rt.userId = :userId AND rt.tenantId = :tenantId"
	)
	public void revokeAllByUserIdAndTenantId(
		@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

	@Modifying
	@Query(
		"UPDATE RefreshTokenEntity rt SET rt.revoked = true WHERE rt.sessionId = :sessionId"
	)
	public void revokeBySessionId(@Param("sessionId") UUID sessionId);

}