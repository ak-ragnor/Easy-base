/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.system.repository;

import com.easybase.infrastructure.data.repository.SingleKeyBaseRepository;
import com.easybase.system.entity.SystemInfo;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for SystemInfo entities with soft delete capabilities.
 * Extends BaseRepository to inherit common data access patterns.
 *
 * @author Akhash R
 */
@Repository
public interface SystemInfoRepository
	extends SingleKeyBaseRepository<SystemInfo> {

	@Query(
		"SELECT s FROM SystemInfo s WHERE s.status = 'ACTIVE' ORDER BY s.createdAt DESC"
	)
	public Optional<SystemInfo> findLatestActive();

	public Optional<SystemInfo> findTopByOrderByCreatedAtDesc();

}