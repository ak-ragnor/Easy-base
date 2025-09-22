/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.repository;

import com.easybase.core.auth.entity.ResourceAction;
import com.easybase.infrastructure.data.repository.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

/**
 * Repository interface for ResourceAction entity.
 *
 * @author Akhash R
 */
@Repository
public interface ResourceActionRepository
	extends BaseRepository<ResourceAction, UUID> {

	public boolean existsByResourceTypeAndActionKey(
		String resourceType, String actionKey);

	public List<ResourceAction> findByIsActiveTrue();

	public List<ResourceAction> findByResourceType(String resourceType);

	public Optional<ResourceAction> findByResourceTypeAndActionKey(
		String resourceType, String actionKey);

	public List<ResourceAction> findByResourceTypeAndIsActiveTrue(
		String resourceType);

}