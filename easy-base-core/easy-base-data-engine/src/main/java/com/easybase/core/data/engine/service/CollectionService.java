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

package com.easybase.core.data.engine.service;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.data.engine.entity.Attribute;
import com.easybase.core.data.engine.entity.Collection;
import com.easybase.core.data.engine.repository.CollectionRepository;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class CollectionService {

	@Transactional
	public Collection createCollection(
		UUID tenantId, String collectionName, List<Attribute> attributes) {

		collectionName = NamingUtils.sanitizeCollectionName(collectionName);

		boolean exists = _collectionRepository.existsByTenantIdAndName(
			tenantId, collectionName);

		if (exists) {
			throw new ConflictException("Collection", "name", collectionName);
		}

		Collection collection = Collection.builder(
		).tenant(
			_getTenant(tenantId)
		).name(
			collectionName
		).build();

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

		log.info(
			"Created collection name={} tenant={}", collectionName, tenantId);

		return collection;
	}

	@Transactional
	public void deleteCollection(UUID collectionId) {
		Collection collection = _getCollection(collectionId);

		Tenant tenant = collection.getTenant();

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
	private final TableManager _tableManager;
	private final TenantRepository _tenantRepository;

}