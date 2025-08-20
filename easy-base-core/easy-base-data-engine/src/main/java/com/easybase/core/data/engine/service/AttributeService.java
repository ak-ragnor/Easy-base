package com.easybase.core.data.engine.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.data.engine.entity.Attribute;
import com.easybase.core.data.engine.entity.Collection;
import com.easybase.core.data.engine.entity.Tenant;
import com.easybase.core.data.engine.enums.AttributeType;
import com.easybase.core.data.engine.repository.AttributeRepository;
import com.easybase.core.data.engine.repository.CollectionRepository;
import com.easybase.core.data.engine.service.ddl.IndexManager;
import com.easybase.core.data.engine.util.NamingUtils;
import com.easybase.core.data.engine.util.TenantSchemaUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttributeService {

	private final CollectionRepository collectionRepository;
	private final AttributeRepository attributeRepository;
	private final IndexManager indexManager;

	@Transactional
	public Attribute addAttribute(UUID collectionId, String attributeName,
			AttributeType dataType, boolean isIndexed) {
		Collection collection = collectionRepository.findById(collectionId)
				.orElseThrow(() -> new ResourceNotFoundException("Collection",
						"id", collectionId));

		String field = NamingUtils.sanitizeAttributeName(attributeName);

		if (attributeRepository.existsByCollectionIdAndName(collectionId,
				attributeName)) {
			throw new ConflictException("Attribute", "name", attributeName);
		}

		Attribute attribute = Attribute.builder().collection(collection)
				.name(attributeName).dataType(dataType).isIndexed(isIndexed)
				.build();

		attribute = attributeRepository.save(attribute);

		if (isIndexed) {
			Tenant tenant = collection.getTenant();

			String schema = TenantSchemaUtil.getSchema(tenant.getId());
			String table = NamingUtils.generateTableName(tenant.getId(),
					collection.getName());

			indexManager.createAttributeIndexIfNotExists(schema, table, field,
					dataType);
		}

		log.info("Added attribute '{}' to collection {}", attributeName,
				collectionId);

		return attribute;
	}
}
