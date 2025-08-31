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

import com.easybase.core.user.entity.UserCredential;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialRepository
	extends JpaRepository<UserCredential, Long> {

	public void deleteByUserId(UUID userId);

	@Query(
		"SELECT uc FROM UserCredential uc WHERE uc.user.email = :email AND uc.passwordType = :type"
	)
	public Optional<UserCredential> findByUserEmailAndType(
		@Param("email") String email, @Param("type") String type);

	public List<UserCredential> findByUserId(UUID userId);

	@Query(
		"SELECT uc FROM UserCredential uc WHERE uc.user.id = :userId AND uc.passwordType = :type"
	)
	public Optional<UserCredential> findByUserIdAndType(
		@Param("userId") UUID userId, @Param("type") String type);

}