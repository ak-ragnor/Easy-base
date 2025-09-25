/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.repository;

import com.easybase.core.role.entity.UserRole;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for UserRole entity.
 *
 * @author Akhash R
 */
@Repository
public interface UserRoleRepository
	extends JpaRepository<UserRole, UserRole.UserRoleId> {

	@Modifying
	@Query(
		"UPDATE UserRole ur SET ur.isActive = false " +
			"WHERE ur.expiresAt <= :currentTime AND ur.isActive = true"
	)
	public int deactivateExpiredRoles(
		@Param("currentTime") Instant currentTime);

	public void deleteByUserIdAndRoleId(UUID userId, UUID roleId);

	public void deleteByRoleId(UUID roleId);

	public boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);

	@Query(
		"SELECT ur.role.id FROM UserRole ur " +
			"WHERE ur.user.id = :userId AND ur.isActive = true " +
				"AND (ur.expiresAt IS NULL OR ur.expiresAt > :currentTime)"
	)
	public List<UUID> findActiveRoleIdsByUserId(
		@Param("userId") UUID userId,
		@Param("currentTime") Instant currentTime);

	@Query(
		"SELECT ur.role.id FROM UserRole ur " +
			"WHERE ur.user.id = :userId AND ur.tenant.id = :tenantId " +
				"AND ur.isActive = true " +
					"AND (ur.expiresAt IS NULL OR ur.expiresAt > :currentTime)"
	)
	public List<UUID> findActiveRoleIdsByUserIdAndTenantId(
		@Param("userId") UUID userId, @Param("tenantId") UUID tenantId,
		@Param("currentTime") Instant currentTime);

	@Query(
		"SELECT ur FROM UserRole ur " +
			"WHERE ur.user.id = :userId AND ur.isActive = true " +
				"AND (ur.expiresAt IS NULL OR ur.expiresAt > :currentTime)"
	)
	public List<UserRole> findActiveUserRoles(
		@Param("userId") UUID userId,
		@Param("currentTime") Instant currentTime);

	public List<UserRole> findByUserIdAndIsActiveTrue(UUID userId);

	public List<UserRole> findByUserIdAndTenantIdAndIsActiveTrue(
		UUID userId, UUID tenantId);

}