/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.repository;

import com.easybase.core.user.entity.User;
import com.easybase.infrastructure.data.repository.TenantAwareRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for User entities with tenant-aware and soft delete capabilities.
 * Extends TenantAwareRepository to inherit common data access patterns.
 *
 * @author Akhash R
 */
@Repository
public interface UserRepository extends TenantAwareRepository<User> {

	public boolean existsByEmailAndTenantId(String email, UUID tenantId);

	@Query(
		"SELECT u FROM User u WHERE u.email = :email AND u.tenant.id = :tenantId AND u.deleted = false"
	)
	public Optional<User> findActiveByEmailAndTenantId(
		@Param("email") String email, @Param("tenantId") UUID tenantId);

}