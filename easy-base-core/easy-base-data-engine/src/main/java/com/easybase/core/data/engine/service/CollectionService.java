package com.easybase.core.data.engine.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionService {

	@Transactional
	public Collection createCollection(UUID tenantId, String collectionName,
			List<Attribute> attributes) {

		collectionName = NamingUtils.sanitizeCollectionName(collectionName);

		if (_collectionRepository.existsByTenantIdAndName(tenantId,
				collectionName)) {
			throw new ConflictException("Collection", "name", collectionName);
		}

		Tenant tenant = _getTenant(tenantId);

		Collection collection = Collection.builder().tenant(tenant)
				.name(collectionName).build();

		if (attributes != null) {
			attributes.forEach(collection::addAttribute);
		}

		collection = _collectionRepository.save(collection);

		String schema = TenantSchemaUtil.getSchema(tenantId);
		String tableName = NamingUtils.generateTableName(tenantId,
				collectionName);

		_tableManager.createTableIfNotExists(schema, tableName);
		_indexManager.createGinIndexIfNotExists(schema, tableName);

		if (attributes != null) {
			attributes.stream()
					.filter(attr -> Boolean.TRUE.equals(attr.getIsIndexed()))
					.forEach(attr -> _indexManager
							.createAttributeIndexIfNotExists(schema, tableName,
									attr.getName(), attr.getDataType()));
		}

		log.info("Created collection name={} tenant={}", collectionName,
				tenantId);
		return collection;
	}

	@Transactional
	public void deleteCollection(UUID collectionId) {
		Collection collection = _getCollection(collectionId);

		Tenant tenant = collection.getTenant();

		String schema = TenantSchemaUtil.getSchema(tenant.getId());
		String table = NamingUtils.generateTableName(tenant.getId(),
				collection.getName());

		_tableManager.dropTableIfExists(schema, table);
		_collectionRepository.delete(collection);

		log.info("Dropped collection name={} tenant={}", collection.getName(),
				tenant.getId());
	}

	@Transactional(readOnly = true)
	public Collection getCollection(UUID collectionId) {
		return _getCollection(collectionId);
	}

	@Transactional(readOnly = true)
	public Collection getCollection(UUID tenantId, String collectionName) {
		return _collectionRepository
				.findByTenantIdAndName(tenantId, collectionName)
				.orElseThrow(() -> new ResourceNotFoundException("Collection",
						"name", collectionName));
	}

	@Transactional(readOnly = true)
	public Page<Collection> getCollections(UUID tenantId, Pageable pageable) {
		return _collectionRepository.findByTenantId(tenantId, pageable);
	}

	@Transactional
	public Collection updateCollection(UUID collectionId,
			List<Attribute> newAttributes) {

		if (newAttributes == null) {
			throw new IllegalArgumentException("newAttributes cannot be null");
		}

		Collection collection = _getCollection(collectionId);

		Tenant tenant = collection.getTenant();

		String schema = TenantSchemaUtil.getSchema(tenant.getId());
		String tableName = NamingUtils.generateTableName(tenant.getId(),
				collection.getName());

		List<Attribute> currentAttributes = collection.getAttributes();

		Map<String, Attribute> currentAttributeMap = currentAttributes.stream()
				.collect(Collectors.toMap(Attribute::getName, attr -> attr,
						(existing, replacement) -> existing));
		Map<String, Attribute> newAttributeMap = newAttributes.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(Attribute::getName, attr -> attr,
						(existing, replacement) -> replacement));

		Set<String> removedAttributes = new HashSet<>(
				currentAttributeMap.keySet());

		removedAttributes.removeAll(newAttributeMap.keySet());

		Set<String> addedAttributes = new HashSet<>(newAttributeMap.keySet());

		addedAttributes.removeAll(currentAttributeMap.keySet());

		Set<String> potentiallyModifiedAttributes = new HashSet<>(
				currentAttributeMap.keySet());

		potentiallyModifiedAttributes.retainAll(newAttributeMap.keySet());

		for (String attrName : removedAttributes) {
			Attribute oldAttr = currentAttributeMap.get(attrName);
			if (Boolean.TRUE.equals(oldAttr.getIsIndexed())) {
				_indexManager.dropAttributeIndexIfExists(schema, tableName,
						attrName);
			}
			collection.removeAttribute(oldAttr);
		}

		for (String attrName : addedAttributes) {
			Attribute newAttr = newAttributeMap.get(attrName);
			collection.addAttribute(newAttr);
			if (Boolean.TRUE.equals(newAttr.getIsIndexed())) {
				_indexManager.createAttributeIndexIfNotExists(schema, tableName,
						attrName, newAttr.getDataType());
			}
		}

		for (String attrName : potentiallyModifiedAttributes) {
			Attribute currentAttr = currentAttributeMap.get(attrName);
			Attribute newAttr = newAttributeMap.get(attrName);

			boolean indexStatusChanged = !Objects
					.equals(currentAttr.getIsIndexed(), newAttr.getIsIndexed());
			boolean dataTypeChanged = !Objects.equals(currentAttr.getDataType(),
					newAttr.getDataType());

			if (indexStatusChanged || dataTypeChanged) {
				if (Boolean.TRUE.equals(currentAttr.getIsIndexed())) {
					_indexManager.dropAttributeIndexIfExists(schema, tableName,
							attrName);
				}

				currentAttr.setDataType(newAttr.getDataType());
				currentAttr.setIsIndexed(newAttr.getIsIndexed());

				if (Boolean.TRUE.equals(newAttr.getIsIndexed())) {
					_indexManager.createAttributeIndexIfNotExists(schema,
							tableName, attrName, newAttr.getDataType());
				}
			}
		}

		collection = _collectionRepository.save(collection);

		log.info("Updated collection name={} tenant={}", collection.getName(),
				tenant.getId());
		return collection;
	}

	private Collection _getCollection(UUID collectionId) {
		return _collectionRepository.findById(collectionId)
				.orElseThrow(() -> new ResourceNotFoundException("Collection",
						"id", collectionId));
	}

	private Tenant _getTenant(UUID tenantId) {
		return _tenantRepository.findById(tenantId).orElseThrow(
				() -> new ResourceNotFoundException("Tenant", "id", tenantId));
	}

	private final TenantRepository _tenantRepository;

	private final CollectionRepository _collectionRepository;

	private final TableManager _tableManager;

	private final IndexManager _indexManager;
}
