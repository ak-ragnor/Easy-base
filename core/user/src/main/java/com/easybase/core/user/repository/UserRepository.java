/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.repository;

import com.easybase.core.user.entity.User;
import com.easybase.infrastructure.data.repository.TenantAwareRepository;

import java.util.List;
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

	@Query(
		"SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.deleted = false"
	)
	public boolean existsActiveByEmail(@Param("email") String email);

	@Query(
		"SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.tenant.id = :tenantId AND u.deleted = false"
	)
	public boolean existsActiveByEmailAndTenantId(
		@Param("email") String email, @Param("tenantId") UUID tenantId);

	public boolean existsByEmail(String email);

	public boolean existsByEmailAndTenantId(String email, UUID tenantId);

	@Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = false")
	public Optional<User> findActiveByEmail(@Param("email") String email);

	@Query(
		"SELECT u FROM User u WHERE u.email = :email AND u.tenant.id = :tenantId AND u.deleted = false"
	)
	public Optional<User> findActiveByEmailAndTenantId(
		@Param("email") String email, @Param("tenantId") UUID tenantId);

	// findActiveByTenantId is inherited from TenantAwareRepository

	public Optional<User> findByEmail(String email);

	public Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

	public List<User> findByTenantId(UUID tenantId);

}