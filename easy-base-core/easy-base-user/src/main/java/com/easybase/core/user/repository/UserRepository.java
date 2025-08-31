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

package com.easybase.core.user.repository;

import com.easybase.core.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

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

	@Query(
		"SELECT u FROM User u WHERE u.tenant.id = :tenantId AND u.deleted = false"
	)
	public List<User> findActiveByTenantId(@Param("tenantId") UUID tenantId);

	public Optional<User> findByEmail(String email);

	public Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

	public List<User> findByTenantId(UUID tenantId);

}