/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.repository;

import com.easybase.core.role.entity.Role;
import com.easybase.infrastructure.data.repository.SingleKeyBaseRepository;

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
public interface RoleRepository extends SingleKeyBaseRepository<Role> {

	public boolean existsByNameAndSystemTrue(String name);

	public boolean existsByNameAndTenantId(String name, UUID tenantId);

	public Optional<Role> findByName(String name);

	public Optional<Role> findByNameAndSystemTrue(String name);

	public Optional<Role> findByNameAndTenantId(String name, UUID tenantId);

	public List<Role> findBySystemTrue();

	public List<Role> findByTenantId(UUID tenantId);

	@Query(
		"SELECT r FROM Role r WHERE (r.system = true) OR (r.tenantId = :tenantId " +
			"AND r.active = true)"
	)
	public List<Role> findSystemAndTenantRoles(
		@Param("tenantId") UUID tenantId);

}