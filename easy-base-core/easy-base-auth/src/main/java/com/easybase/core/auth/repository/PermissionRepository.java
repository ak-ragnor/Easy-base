/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.repository;

import com.easybase.core.auth.entity.Permission;
import com.easybase.infrastructure.data.repository.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Permission entity.
 *
 * @author Akhash R
 */
@Repository
public interface PermissionRepository extends BaseRepository<Permission, UUID> {

	public boolean existsByPermissionKey(String permissionKey);

	public boolean existsByResourceTypeAndAction(
		String resourceType, String action);

	@Query(
		"SELECT p FROM Permission p " + "JOIN p.rolePermissions rp " +
			"JOIN rp.role r " + "JOIN r.userRoles ur " +
				"WHERE ur.user.id = :userId " + "AND ur.isActive = true " +
					"AND r.isActive = true"
	)
	public List<Permission> findActivePermissionsByUserId(
		@Param("userId") UUID userId);

	@Query(
		"SELECT p FROM Permission p " + "JOIN p.rolePermissions rp " +
			"JOIN rp.role r " + "JOIN r.userRoles ur " +
				"WHERE ur.user.id = :userId " +
					"AND ur.tenant.id = :tenantId " +
						"AND ur.isActive = true " + "AND r.isActive = true"
	)
	public List<Permission> findActivePermissionsByUserIdAndTenantId(
		@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

	public Optional<Permission> findByPermissionKey(String permissionKey);

	public List<Permission> findByResourceType(String resourceType);

	public Optional<Permission> findByResourceTypeAndAction(
		String resourceType, String action);

}