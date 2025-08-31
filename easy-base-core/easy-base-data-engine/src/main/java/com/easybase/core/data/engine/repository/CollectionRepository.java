/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.repository;

import com.easybase.core.data.engine.entity.Collection;
import com.easybase.core.tenant.entity.Tenant;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Akhash R
 */
@Repository
public interface CollectionRepository extends JpaRepository<Collection, UUID> {

	public boolean existsByTenantAndName(Tenant tenant, String name);

	public boolean existsByTenantIdAndName(UUID tenantId, String name);

	public Optional<Collection> findByTenantAndName(Tenant tenant, String name);

	public Page<Collection> findByTenantId(UUID tenantId, Pageable pageable);

	public Optional<Collection> findByTenantIdAndName(
		UUID tenantId, String name);

}