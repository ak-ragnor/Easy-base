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