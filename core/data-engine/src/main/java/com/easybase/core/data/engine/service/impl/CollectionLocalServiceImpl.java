/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.impl;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.auth.entity.ResourceAction;
import com.easybase.core.auth.service.ResourceActionLocalService;
import com.easybase.core.data.engine.entity.Attribute;
import com.easybase.core.data.engine.entity.Collection;
import com.easybase.core.data.engine.repository.CollectionRepository;
import com.easybase.core.data.engine.service.CollectionLocalService;
import com.easybase.core.data.engine.service.ddl.IndexManager;
import com.easybase.core.data.engine.service.ddl.TableManager;
import com.easybase.core.data.engine.util.NamingUtils;
import com.easybase.core.data.engine.util.TenantSchemaUtil;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.repository.TenantRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link CollectionLocalService}.
 * Contains all business logic, repository calls, and transaction management.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class CollectionLocalServiceImpl implements CollectionLocalService {

	@Transactional
	public Collection createCollection(
		UUID tenantId, String collectionName, List<Attribute> attributes) {

		collectionName = NamingUtils.sanitizeCollectionName(collectionName);

		boolean exists = _collectionRepository.existsByTenantIdAndName(
			tenantId, collectionName);

		if (exists) {
			throw new ConflictException("Collection", "name", collectionName);
		}

		Collection collection = new Collection();

		collection.setTenant(_getTenant(tenantId));
		collection.setName(collectionName);

		if (attributes != null) {
			for (Attribute attribute : attributes) {
				collection.addAttribute(attribute);
			}
		}

		collection = _collectionRepository.save(collection);

		String schema = TenantSchemaUtil.getSchema(tenantId);
		String tableName = NamingUtils.generateTableName(
			tenantId, collectionName);

		_tableManager.createTableIfNotExists(schema, tableName);
		_indexManager.createGinIndexIfNotExists(schema, tableName);

		if (attributes != null) {
			for (Attribute attr : attributes) {
				if (Boolean.TRUE.equals(attr.getIndexed())) {
					_indexManager.createAttributeIndexIfNotExists(
						schema, tableName, attr.getName(), attr.getDataType());
				}
			}
		}

		_createResourceActions(collectionName);

		log.info(
			"Created collection name={} tenant={}", collectionName, tenantId);

		return collection;
	}

	@Transactional
	public void deleteCollection(UUID collectionId) {
		Collection collection = _getCollection(collectionId);

		Tenant tenant = collection.getTenant();

		_deleteResourceActions(collection.getName());

		_tableManager.dropTableIfExists(
			TenantSchemaUtil.getSchema(tenant.getId()),
			NamingUtils.generateTableName(
				tenant.getId(), collection.getName()));

		_collectionRepository.delete(collection);

		log.info(
			"Dropped collection name={} tenant={}", collection.getName(),
			tenant.getId());
	}

	@Transactional(readOnly = true)
	public Collection getCollection(UUID collectionId) {
		return _getCollection(collectionId);
	}

	@Transactional(readOnly = true)
	public Collection getCollection(UUID tenantId, String collectionName) {
		var collectionOptional = _collectionRepository.findByTenantIdAndName(
			tenantId, collectionName);

		if (collectionOptional.isEmpty()) {
			throw new ResourceNotFoundException(
				"Collection", "name", collectionName);
		}

		return collectionOptional.get();
	}

	@Transactional(readOnly = true)
	public Page<Collection> getCollections(UUID tenantId, Pageable pageable) {
		return _collectionRepository.findByTenantId(tenantId, pageable);
	}

	@Transactional
	public Collection updateCollection(
		UUID collectionId, List<Attribute> newAttributes) {

		if (newAttributes == null) {
			throw new IllegalArgumentException("newAttributes cannot be null");
		}

		Collection collection = _getCollection(collectionId);

		Tenant tenant = collection.getTenant();

		String schema = TenantSchemaUtil.getSchema(tenant.getId());
		String tableName = NamingUtils.generateTableName(
			tenant.getId(), collection.getName());

		List<Attribute> currentAttributes = collection.getAttributes();

		Map<String, Attribute> currentAttributeMap = _toAttributeMap(
			currentAttributes, false);

		Map<String, Attribute> newAttributeMap = _toAttributeMap(
			newAttributes, true);

		Set<String> removedAttributes = new HashSet<>(
			currentAttributeMap.keySet());

		removedAttributes.removeAll(newAttributeMap.keySet());

		Set<String> addedAttributes = new HashSet<>(newAttributeMap.keySet());

		addedAttributes.removeAll(currentAttributeMap.keySet());

		Set<String> potentiallyModifiedAttributes = new HashSet<>(
			currentAttributeMap.keySet());

		potentiallyModifiedAttributes.retainAll(newAttributeMap.keySet());

		// Remove old attributes

		for (String attrName : removedAttributes) {
			Attribute oldAttr = currentAttributeMap.get(attrName);

			if (Boolean.TRUE.equals(oldAttr.getIndexed())) {
				_indexManager.dropAttributeIndexIfExists(
					schema, tableName, attrName);
			}

			collection.removeAttribute(oldAttr);
		}

		// Add new attributes

		for (String attrName : addedAttributes) {
			Attribute newAttr = newAttributeMap.get(attrName);

			collection.addAttribute(newAttr);

			if (Boolean.TRUE.equals(newAttr.getIndexed())) {
				_indexManager.createAttributeIndexIfNotExists(
					schema, tableName, attrName, newAttr.getDataType());
			}
		}

		// Update existing attributes

		for (String attrName : potentiallyModifiedAttributes) {
			Attribute currentAttr = currentAttributeMap.get(attrName);
			Attribute newAttr = newAttributeMap.get(attrName);

			boolean indexChanged = !Objects.equals(
				currentAttr.getIndexed(), newAttr.getIndexed());
			boolean typeChanged = !Objects.equals(
				currentAttr.getDataType(), newAttr.getDataType());

			if (indexChanged || typeChanged) {
				if (Boolean.TRUE.equals(currentAttr.getIndexed())) {
					_indexManager.dropAttributeIndexIfExists(
						schema, tableName, attrName);
				}

				currentAttr.setDataType(newAttr.getDataType());
				currentAttr.setIndexed(newAttr.getIndexed());

				if (Boolean.TRUE.equals(newAttr.getIndexed())) {
					_indexManager.createAttributeIndexIfNotExists(
						schema, tableName, attrName, newAttr.getDataType());
				}
			}
		}

		collection = _collectionRepository.save(collection);

		log.info(
			"Updated collection name={} tenant={}", collection.getName(),
			tenant.getId());

		return collection;
	}

	private void _createResourceActions(String collectionName) {
		String resourceType = collectionName.toUpperCase();

		String[] actions = {"CREATE", "READ", "UPDATE", "DELETE"};
		int bitValue = 1;

		for (String action : actions) {
			String actionKey = resourceType + ":" + action;

			if (!_resourceActionLocalService.resourceActionExists(
					resourceType, actionKey)) {

				_resourceActionLocalService.createResourceAction(
					resourceType, actionKey, action, bitValue,
					action + " permission for " + collectionName +
						" collection");

				log.debug(
					"Created resource action: {} with bit value: {}", actionKey,
					bitValue);
			}

			bitValue = bitValue << 1;
		}
	}

	private void _deleteResourceActions(String collectionName) {
		String resourceType = collectionName.toUpperCase();

		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(resourceType);

		for (ResourceAction resourceAction : resourceActions) {
			_resourceActionLocalService.deleteResourceAction(
				resourceAction.getId());

			log.debug(
				"Deleted resource action: {}", resourceAction.getActionKey());
		}
	}

	private Collection _getCollection(UUID collectionId) {
		var collectionOptional = _collectionRepository.findById(collectionId);

		if (collectionOptional.isEmpty()) {
			throw new ResourceNotFoundException(
				"Collection", "id", collectionId);
		}

		return collectionOptional.get();
	}

	private Tenant _getTenant(UUID tenantId) {
		var tenantOptional = _tenantRepository.findById(tenantId);

		if (tenantOptional.isEmpty()) {
			throw new ResourceNotFoundException("Tenant", "id", tenantId);
		}

		return tenantOptional.get();
	}

	private Map<String, Attribute> _toAttributeMap(
		List<Attribute> attributes, boolean overwriteDuplicates) {

		Map<String, Attribute> map = new HashMap<>();

		for (Attribute attr : attributes) {
			if ((attr != null) &&
				(overwriteDuplicates || !map.containsKey(attr.getName()))) {

				map.put(attr.getName(), attr);
			}
		}

		return map;
	}

	private final CollectionRepository _collectionRepository;
	private final IndexManager _indexManager;
	private final ResourceActionLocalService _resourceActionLocalService;
	private final TableManager _tableManager;
	private final TenantRepository _tenantRepository;

}