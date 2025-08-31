/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
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