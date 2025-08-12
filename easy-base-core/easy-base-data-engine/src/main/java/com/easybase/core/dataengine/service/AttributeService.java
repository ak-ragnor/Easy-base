package com.easybase.core.dataengine.service;

import com.easybase.core.dataengine.entity.Attribute;
import com.easybase.core.dataengine.entity.Collection;
import com.easybase.core.dataengine.enums.AttributeType;
import com.easybase.core.dataengine.repository.AttributeRepository;
import com.easybase.core.dataengine.repository.CollectionRepository;
import com.easybase.core.dataengine.service.ddl.IndexManager;
import com.easybase.core.dataengine.util.NamingUtils;
import com.easybase.core.dataengine.util.TenantSchemaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttributeService {

    private final CollectionRepository collectionRepository;
    private final AttributeRepository attributeRepository;
    private final IndexManager indexManager;

    @Transactional
    public Attribute addAttribute(UUID collectionId, String attributeName, AttributeType dataType, boolean isIndexed) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found: " + collectionId));

        String field = NamingUtils.sanitizeAttributeName(attributeName);

        if (attributeRepository.existsByCollectionIdAndName(collectionId, attributeName)) {
            throw new IllegalArgumentException("Attribute '" + attributeName + "' already exists in collection: " + collectionId);
        }

        Attribute attribute = Attribute.builder()
                .collection(collection)
                .name(attributeName)
                .dataType(dataType)
                .isIndexed(isIndexed)
                .build();
        attribute = attributeRepository.save(attribute);

        if (isIndexed) {
            String schema = TenantSchemaUtil.getSchema(collection.getTenant().getId());
            String table = NamingUtils.sanitizeCollectionName(collection.getName());
            indexManager.createAttributeIndexIfNotExists(schema, table, field, dataType);
        }

        log.info("Added attribute '{}' to collection {}", attributeName, collectionId);
        return attribute;
    }
}
