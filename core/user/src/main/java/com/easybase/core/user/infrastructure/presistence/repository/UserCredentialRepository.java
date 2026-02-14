/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.infrastructure.presistence.repository;

import com.easybase.core.user.domain.entity.UserCredential;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Akhash R
 */
@Repository
public interface UserCredentialRepository
	extends JpaRepository<UserCredential, Long> {

	public List<UserCredential> findByUserId(UUID userId);

	@Query(
		"SELECT uc FROM UserCredential uc WHERE uc.user.id = :userId AND uc.passwordType = :type"
	)
	public Optional<UserCredential> findByUserIdAndType(
		@Param("userId") UUID userId, @Param("type") String type);

}