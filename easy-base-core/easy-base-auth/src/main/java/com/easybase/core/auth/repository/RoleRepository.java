/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.repository;

import com.easybase.core.auth.entity.Role;
import com.easybase.infrastructure.data.repository.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Role entity.
 *
 * @author Akhash R
 */
@Repository
public interface RoleRepository extends BaseRepository<Role, UUID> {

	public boolean existsByNameAndIsSystemTrue(String name);

	public boolean existsByNameAndTenantId(String name, UUID tenantId);

	@Query(
		"SELECT DISTINCT r FROM Role r " + "LEFT JOIN FETCH r.permissions p " +
			"LEFT JOIN FETCH p.permission " + "WHERE r.id = :roleId"
	)
	public Optional<Role> findByIdWithPermissions(@Param("roleId") UUID roleId);

	public List<Role> findByIsSystemTrue();

	public Optional<Role> findByNameAndIsSystemTrue(String name);

	public Optional<Role> findByNameAndTenantId(String name, UUID tenantId);

	public List<Role> findByTenantId(UUID tenantId);

	@Query(
		"SELECT r FROM Role r WHERE r.isSystem = true OR r.tenant.id = :tenantId"
	)
	public List<Role> findSystemAndTenantRoles(
		@Param("tenantId") UUID tenantId);

}