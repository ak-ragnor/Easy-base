/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.impl;

import com.easybase.context.api.util.PermissionChecker;
import com.easybase.core.data.engine.action.CollectionActions;
import com.easybase.core.data.engine.domain.entity.Attribute;
import com.easybase.core.data.engine.domain.entity.Collection;
import com.easybase.core.data.engine.service.CollectionLocalService;
import com.easybase.core.data.engine.service.CollectionService;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link CollectionService}.
 * ALWAYS performs permission checks before delegating to CollectionLocalService.
 * Never performs persistence directly - always delegates to CollectionLocalService.
 *
 * <p>If permission checks are not needed, use CollectionLocalService directly.</p>
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
public class CollectionServiceImpl implements CollectionService {

	@Override
	public Collection createCollection(
		UUID tenantId, String collectionName, List<Attribute> attributes) {

		_permissionChecker.check(CollectionActions.COLLECTION_CREATE);

		return _collectionLocalService.createCollection(
			tenantId, collectionName, attributes);
	}

	@Override
	public void deleteCollection(UUID collectionId) {
		_permissionChecker.check(CollectionActions.COLLECTION_DELETE);

		_collectionLocalService.deleteCollection(collectionId);
	}

	@Override
	public Collection getCollection(UUID collectionId) {
		_permissionChecker.check(CollectionActions.COLLECTION_VIEW);

		return _collectionLocalService.getCollection(collectionId);
	}

	@Override
	public Collection getCollection(UUID tenantId, String collectionName) {
		_permissionChecker.check(CollectionActions.COLLECTION_VIEW);

		return _collectionLocalService.getCollection(tenantId, collectionName);
	}

	@Override
	public Page<Collection> getCollections(UUID tenantId, Pageable pageable) {
		_permissionChecker.check(CollectionActions.COLLECTION_LIST);

		return _collectionLocalService.getCollections(tenantId, pageable);
	}

	@Override
	public Collection updateCollection(
		UUID collectionId, List<Attribute> newAttributes) {

		_permissionChecker.check(CollectionActions.COLLECTION_UPDATE);

		return _collectionLocalService.updateCollection(
			collectionId, newAttributes);
	}

	private final CollectionLocalService _collectionLocalService;
	private final PermissionChecker _permissionChecker;

}