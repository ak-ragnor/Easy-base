/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service;

import com.easybase.core.data.engine.entity.Attribute;
import com.easybase.core.data.engine.entity.Collection;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * External-facing service interface for collection operations.
 * Performs permission checks before delegating to CollectionLocalService.
 * Never performs persistence directly - always delegates to CollectionLocalService.
 *
 * @author Akhash R
 */
public interface CollectionService {

	/**
	 * Creates a new collection.
	 * Requires COLLECTION:CREATE permission.
	 *
	 * @param tenantId the tenant ID
	 * @param collectionName the collection name
	 * @param attributes the collection attributes
	 * @return the created collection
	 * @throws com.easybase.common.exception.ConflictException if collection already exists
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	Collection createCollection(
		UUID tenantId, String collectionName, List<Attribute> attributes);

	/**
	 * Deletes a collection.
	 * Requires COLLECTION:DELETE permission.
	 *
	 * @param collectionId the collection ID
	 * @throws com.easybase.common.exception.ResourceNotFoundException if collection not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	void deleteCollection(UUID collectionId);

	/**
	 * Gets a collection by ID.
	 * Requires COLLECTION:VIEW permission.
	 *
	 * @param collectionId the collection ID
	 * @return the collection
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	Collection getCollection(UUID collectionId);

	/**
	 * Gets a collection by tenant and name.
	 * Requires COLLECTION:VIEW permission.
	 *
	 * @param tenantId the tenant ID
	 * @param collectionName the collection name
	 * @return the collection
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	Collection getCollection(UUID tenantId, String collectionName);

	/**
	 * Gets collections for a tenant with pagination.
	 * Requires COLLECTION:LIST permission.
	 *
	 * @param tenantId the tenant ID
	 * @param pageable the pagination parameters
	 * @return page of collections
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	Page<Collection> getCollections(UUID tenantId, Pageable pageable);

	/**
	 * Updates a collection's attributes.
	 * Requires COLLECTION:UPDATE permission.
	 *
	 * @param collectionId the collection ID
	 * @param newAttributes the new attributes
	 * @return the updated collection
	 * @throws com.easybase.common.exception.ResourceNotFoundException if collection not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	Collection updateCollection(
		UUID collectionId, List<Attribute> newAttributes);

}