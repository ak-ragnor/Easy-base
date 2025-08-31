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