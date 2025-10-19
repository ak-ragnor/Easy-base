/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.repository;

import com.easybase.core.auth.entity.ResourceAction;
import com.easybase.infrastructure.data.repository.SingleKeyBaseRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/**
 * Repository interface for ResourceAction entity.
 *
 * @author Akhash R
 */
@Repository
public interface ResourceActionRepository
	extends SingleKeyBaseRepository<ResourceAction> {

	public boolean existsByResourceTypeAndActionKey(
		String resourceType, String actionKey);

	public Optional<ResourceAction> findByActionKey(String actionKey);

	public List<ResourceAction> findByActiveTrue();

	public List<ResourceAction> findByResourceType(String resourceType);

	public Optional<ResourceAction> findByResourceTypeAndActionKey(
		String resourceType, String actionKey);

	public List<ResourceAction> findByResourceTypeAndActiveTrue(
		String resourceType);

	public List<ResourceAction> findByResourceTypeOrderByBitValue(
		String resourceType);

}