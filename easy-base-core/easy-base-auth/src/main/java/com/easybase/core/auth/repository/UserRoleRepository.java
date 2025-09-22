/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.repository;

import com.easybase.core.auth.entity.UserRole;
import com.easybase.infrastructure.data.repository.BaseRepository;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
	extends BaseRepository<UserRole, UserRole.UserRoleId> {

	@Modifying
	@Query(
		"UPDATE UserRole ur SET ur.isActive = false WHERE ur.expiresAt < :now"
	)
	public int deactivateExpiredRoles(@Param("now") Instant now);

	public void deleteByUserIdAndRoleId(UUID userId, UUID roleId);

	public boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);

	@Query(
		"SELECT ur FROM UserRole ur " + "JOIN FETCH ur.role r " +
			"WHERE ur.user.id = :userId " + "AND ur.isActive = true " +
				"AND (ur.expiresAt IS NULL OR ur.expiresAt > :now)"
	)
	public List<UserRole> findActiveUserRoles(
		@Param("userId") UUID userId, @Param("now") Instant now);

	@Query(
		"SELECT ur FROM UserRole ur " + "JOIN FETCH ur.role r " +
			"LEFT JOIN FETCH r.permissions p " +
				"LEFT JOIN FETCH p.permission " +
					"WHERE ur.user.id = :userId " +
						"AND ur.tenant.id = :tenantId " +
							"AND ur.isActive = true " +
								"AND (ur.expiresAt IS NULL OR ur.expiresAt > :now)"
	)
	public List<UserRole> findActiveUserRolesWithPermissions(
		@Param("userId") UUID userId, @Param("tenantId") UUID tenantId,
		@Param("now") Instant now);

	public List<UserRole> findByRoleId(UUID roleId);

	public List<UserRole> findByUserId(UUID userId);

	public List<UserRole> findByUserIdAndIsActiveTrue(UUID userId);

	public Optional<UserRole> findByUserIdAndRoleId(UUID userId, UUID roleId);

	public List<UserRole> findByUserIdAndTenantId(UUID userId, UUID tenantId);

	public List<UserRole> findByUserIdAndTenantIdAndIsActiveTrue(
		UUID userId, UUID tenantId);

}