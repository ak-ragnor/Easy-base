package com.easybase.core.dataengine.service;

import com.easybase.core.dataengine.entity.Attribute;
import com.easybase.core.dataengine.entity.Collection;
import com.easybase.core.dataengine.entity.Tenant;
import com.easybase.core.dataengine.repository.AttributeRepository;
import com.easybase.core.dataengine.repository.CollectionRepository;
import com.easybase.core.dataengine.repository.TenantRepository;
import com.easybase.core.dataengine.service.ddl.IndexManager;
import com.easybase.core.dataengine.service.ddl.SchemaManager;
import com.easybase.core.dataengine.service.ddl.TableManager;
import com.easybase.core.dataengine.util.NamingUtils;
import com.easybase.core.dataengine.util.TenantSchemaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionService {

    private final TenantRepository tenantRepository;
    private final CollectionRepository collectionRepository;
    private final AttributeRepository attributeRepository;

    private final SchemaManager schemaManager;
    private final TableManager tableManager;
    private final IndexManager indexManager;

    @Transactional
    public Collection createCollection(UUID tenantId, String collectionName, List<Attribute> attributes) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));

        collectionName = NamingUtils.sanitizeCollectionName(collectionName);

        if (collectionRepository.existsByTenantIdAndName(tenantId, collectionName)) {
            throw new IllegalArgumentException("Collection '" + collectionName + "' already exists for tenant: " + tenantId);
        }

        Collection collection = Collection.builder()
                .tenant(tenant)
                .name(collectionName)
                .build();
        collection = collectionRepository.save(collection);

        for (Attribute attribute : attributes) {
            attribute.setCollection(collection);
            attributeRepository.save(attribute);
        }

        String schema = TenantSchemaUtil.getSchema(tenantId);
        tableManager.createTableIfNotExists(schema, collectionName);

        for (Attribute attribute : attributes) {
            if (Boolean.TRUE.equals(attribute.getIsIndexed())) {
                indexManager.createAttributeIndexIfNotExists(schema, collectionName, attribute.getName(), attribute.getDataType());
            }
        }

        indexManager.createGinIndexIfNotExists(schema, collectionName);

        log.info("Created collection '{}' for tenant {}", collectionName, tenantId);
        return collection;
    }

    @Transactional
    public void dropCollection(UUID collectionId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found: " + collectionId));

        String schema = TenantSchemaUtil.getSchema(collection.getTenant().getId());
        String table = NamingUtils.sanitizeCollectionName(collection.getName());

        tableManager.dropTableIfExists(schema, table);
        collectionRepository.delete(collection);

        log.info("Dropped collection '{}' (tenant {})", collection.getName(), collection.getTenant().getId());
    }
}
