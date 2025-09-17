/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.data.repository;

import com.easybase.infrastructure.data.entity.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * Base repository interface for tenant-aware entities that need multi-tenancy
 * support. Extends BaseRepository with additional tenant-scoped operations.
 *
 * This interface assumes the entity has a "tenant" or "tenantId" field for
 * tenant isolation. Implementations should ensure all queries are properly
 * tenant-scoped for security and data isolation.
 *
 * @param <T> the entity type that extends BaseEntity and is tenant-aware
 * @author Akhash R
 */
@NoRepositoryBean
public interface TenantAwareRepository<T extends BaseEntity>
	extends BaseRepository<T> {

	/**
	 * Checks if an active entity exists for the given tenant.
	 *
	 * @param tenantId the tenant ID to check within
	 * @param id the entity ID to check
	 * @return true if an active entity exists with the given ID in the tenant
	 */
	@Query(
		"SELECT COUNT(e) > 0 FROM #{#entityName} e WHERE e.tenant.id = :tenantId AND e.id = :id AND e.deleted = false"
	)
	public boolean existsActiveByIdAndTenantId(
		@Param("tenantId") UUID tenantId, @Param("id") UUID id);

	/**
	 * Finds an active entity by ID within a specific tenant.
	 *
	 * @param tenantId the tenant ID to filter by
	 * @param id the entity ID to find
	 * @return optional containing the entity if found and active within tenant
	 */
	@Query(
		"SELECT e FROM #{#entityName} e WHERE e.tenant.id = :tenantId AND e.id = :id AND e.deleted = false"
	)
	public Optional<T> findActiveByIdAndTenantId(
		@Param("tenantId") UUID tenantId, @Param("id") UUID id);

	/**
	 * Finds all active entities within a specific tenant.
	 *
	 * @param tenantId the tenant ID to filter by
	 * @return list of active entities within the tenant
	 */
	@Query(
		"SELECT e FROM #{#entityName} e WHERE e.tenant.id = :tenantId AND e.deleted = false ORDER BY e.updatedAt DESC"
	)
	public List<T> findActiveByTenantId(@Param("tenantId") UUID tenantId);

}