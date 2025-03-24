package com.easybase.core.collection;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import com.easybase.common.exception.EntityNotFoundException;
import com.easybase.core.storage.DataSourceManager;
import com.easybase.core.sync.SyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of CollectionService that uses JdbcTemplate for database operations
 * and Elasticsearch for search operations.
 */
@Service
public class CollectionServiceImpl implements CollectionService {

    private static final Logger logger = LoggerFactory.getLogger(CollectionServiceImpl.class);

    @Autowired
    private DataSourceManager databaseService;

    @Autowired
    private ElasticsearchClient esClient;

    @Autowired
    private SyncService syncService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // Cache of registered entity definitions
    private final Map<String, EntityDefinition> entityDefinitions = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        loadExistingCollections();
    }

    /**
     * Loads existing collections from the database.
     */
    private void loadExistingCollections() {
        try {
            // Load collection metadata from a system table
            List<EntityDefinition> definitions = jdbcTemplate.query(
                    "SELECT * FROM eb_collection_metadata",
                    (rs, rowNum) -> {
                        String name = rs.getString("name");
                        String tableName = rs.getString("table_name");
                        EntityDefinition definition = new EntityDefinition(name, tableName);

                        // Load field definitions
                        List<FieldDefinition> fields = loadFieldDefinitions(tableName);
                        definition.setFields(fields);

                        entityDefinitions.put(name, definition);
                        return definition;
                    }
            );

            logger.info("Loaded {} collections from database", entityDefinitions.size());
        } catch (DataAccessException e) {
            logger.warn("Failed to load existing collections: {}", e.getMessage());
            // Create metadata table if it doesn't exist
            createMetadataTable();
        }
    }

    /**
     * Creates the metadata table if it doesn't exist.
     */
    private void createMetadataTable() {
        try {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS eb_collection_metadata (" +
                            "name VARCHAR(255) PRIMARY KEY, " +
                            "table_name VARCHAR(255) NOT NULL, " +
                            "created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                            "modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );

            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS eb_field_metadata (" +
                            "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                            "collection_name VARCHAR(255) NOT NULL, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "type VARCHAR(50) NOT NULL, " +
                            "is_primary_key BOOLEAN NOT NULL DEFAULT FALSE, " +
                            "is_nullable BOOLEAN NOT NULL DEFAULT TRUE, " +
                            "search_mapping TEXT, " +
                            "CONSTRAINT uk_field_collection UNIQUE (collection_name, name)" +
                            ")"
            );

            logger.info("Created collection metadata tables");
        } catch (DataAccessException e) {
            logger.error("Failed to create metadata tables: {}", e.getMessage(), e);
        }
    }

    /**
     * Loads field definitions for a collection.
     *
     * @param tableName the table name
     * @return the list of field definitions
     */
    private List<FieldDefinition> loadFieldDefinitions(String tableName) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM eb_field_metadata WHERE collection_name = ?",
                    (rs, rowNum) -> {
                        FieldDefinition field = new FieldDefinition();
                        field.setName(rs.getString("name"));
                        field.setType(rs.getString("type"));
                        field.setPrimaryKey(rs.getBoolean("is_primary_key"));
                        field.setNullable(rs.getBoolean("is_nullable"));

                        // Parse searchMapping if present
                        String searchMapping = rs.getString("search_mapping");
                        if (searchMapping != null && !searchMapping.isEmpty()) {
                            try {
                                Map<String, Object> mapping = objectMapper.readValue(searchMapping, Map.class);
                                field.setSearchMapping(mapping);
                            } catch (IOException e) {
                                logger.warn("Failed to parse search mapping for field {}: {}",
                                        field.getName(), e.getMessage());
                            }
                        }

                        return field;
                    },
                    tableName
            );
        } catch (DataAccessException e) {
            logger.warn("Failed to load field definitions for table {}: {}", tableName, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public EntityDefinition createCollection(String collectionName, List<FieldDefinition> fieldDefinitions) {
        // Validate collection name
        if (entityDefinitions.containsKey(collectionName)) {
            throw new IllegalArgumentException("Collection already exists: " + collectionName);
        }

        // Create entity definition
        EntityDefinition definition = new EntityDefinition(collectionName);
        definition.setFields(fieldDefinitions);

        // Create database table
        createDatabaseTable(definition);

        // Create Elasticsearch index
        createElasticsearchIndex(definition);

        // Save metadata
        saveCollectionMetadata(definition);

        // Register the entity definition
        entityDefinitions.put(collectionName, definition);

        return definition;
    }

    /**
     * Creates a database table for the collection.
     *
     * @param definition the entity definition
     */
    private void createDatabaseTable(EntityDefinition definition) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(definition.getTableName()).append(" (");

        // Add fields
        List<String> columnDefs = new ArrayList<>();
        for (FieldDefinition field : definition.getFields()) {
            columnDefs.add(field.getName() + " " + field.getSqlDefinition());
        }

        // Add standard fields
        columnDefs.add("created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
        columnDefs.add("modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

        sql.append(String.join(", ", columnDefs));
        sql.append(")");

        try {
            jdbcTemplate.execute(sql.toString());
            logger.info("Created database table: {}", definition.getTableName());
        } catch (DataAccessException e) {
            logger.error("Failed to create database table: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create database table: " + e.getMessage(), e);
        }
    }

    /**
     * Creates an Elasticsearch index for the collection.
     *
     * @param definition the entity definition
     */
    private void createElasticsearchIndex(EntityDefinition definition) {
        try {
            // Create mapping properties from field definitions
            Map<String, Property> properties = new HashMap<>();

            for (FieldDefinition field : definition.getFields()) {
                Map<String, Object> fieldMapping = field.getSearchMapping();
                if (fieldMapping != null && !fieldMapping.isEmpty()) {
                    Property property = Property.of(p -> {
                        // Convert from Map<String, Object> to Property
                        // The implementation depends on the specific field type
                        String type = (String) fieldMapping.get("type");
                        if ("text".equals(type)) {
                            return p.text(t -> {
                                // Set analyzer if specified
                                if (fieldMapping.containsKey("analyzer")) {
                                    t.analyzer((String) fieldMapping.get("analyzer"));
                                }
                                return t;
                            });
                        } else if ("keyword".equals(type)) {
                            return p.keyword(k -> k);
                        } else if ("date".equals(type)) {
                            return p.date(d -> d);
                        } else if ("nested".equals(type)) {
                            return p.nested(n -> n);
                        } else {
                            // Default to keyword
                            return p.keyword(k -> k);
                        }
                    });

                    properties.put(field.getName(), property);
                }
            }

            // Add standard fields
            properties.put("created_date", Property.of(p -> p.date(d -> d)));
            properties.put("modified_date", Property.of(p -> p.date(d -> d)));

            // Create the index with mappings
            CreateIndexRequest request = CreateIndexRequest.of(r -> r
                    .index(definition.getIndexName())
                    .mappings(m -> m
                            .properties(properties)
                    )
            );

            esClient.indices().create(request);
            logger.info("Created Elasticsearch index: {}", definition.getIndexName());
        } catch (IOException e) {
            logger.error("Failed to create Elasticsearch index: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Elasticsearch index: " + e.getMessage(), e);
        }
    }

    /**
     * Saves collection metadata to the database.
     *
     * @param definition the entity definition
     */
    private void saveCollectionMetadata(EntityDefinition definition) {
        try {
            // Save collection metadata
            jdbcTemplate.update(
                    "INSERT INTO eb_collection_metadata (name, table_name) VALUES (?, ?)",
                    definition.getName(),
                    definition.getTableName()
            );

            // Save field metadata
            for (FieldDefinition field : definition.getFields()) {
                String searchMappingJson = null;
                if (field.getSearchMapping() != null && !field.getSearchMapping().isEmpty()) {
                    searchMappingJson = objectMapper.writeValueAsString(field.getSearchMapping());
                }

                jdbcTemplate.update(
                        "INSERT INTO eb_field_metadata (collection_name, name, type, is_primary_key, is_nullable, search_mapping) VALUES (?, ?, ?, ?, ?, ?)",
                        definition.getTableName(),
                        field.getName(),
                        field.getType(),
                        field.isPrimaryKey(),
                        field.isNullable(),
                        searchMappingJson
                );
            }

            logger.info("Saved metadata for collection: {}", definition.getName());
        } catch (DataAccessException | IOException e) {
            logger.error("Failed to save collection metadata: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save collection metadata: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public EntityDefinition updateCollection(String collectionName, List<FieldDefinition> fieldDefinitions) {
        EntityDefinition definition = getEntityDefinition(collectionName);

        // Find new fields
        List<FieldDefinition> newFields = fieldDefinitions.stream()
                .filter(field -> definition.getField(field.getName()) == null)
                .collect(Collectors.toList());

        // TODO: Implement schema migration logic

        return definition;
    }

    @Override
    @Transactional
    public boolean deleteCollection(String collectionName) {
        EntityDefinition definition = getEntityDefinition(collectionName);

        try {
            // Drop the table
            jdbcTemplate.execute("DROP TABLE IF EXISTS " + definition.getTableName());

            // Delete the index
            esClient.indices().delete(d -> d.index(definition.getIndexName()));

            // Delete metadata
            jdbcTemplate.update("DELETE FROM eb_field_metadata WHERE collection_name = ?", definition.getTableName());
            jdbcTemplate.update("DELETE FROM eb_collection_metadata WHERE name = ?", collectionName);

            // Remove from cache
            entityDefinitions.remove(collectionName);

            logger.info("Deleted collection: {}", collectionName);
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete collection: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Optional<EntityDefinition> getCollection(String collectionName) {
        return Optional.ofNullable(entityDefinitions.get(collectionName));
    }

    @Override
    public List<EntityDefinition> listCollections() {
        return new ArrayList<>(entityDefinitions.values());
    }

    @Override
    @Transactional
    public Map<String, Object> createRecord(String collectionName, Map<String, Object> recordData) {
        EntityDefinition definition = getEntityDefinition(collectionName);

        // Generate ID if not provided
        if (!recordData.containsKey("id")) {
            recordData.put("id", UUID.randomUUID().toString());
        }

        // Validate data against field definitions
        validateData(definition, recordData);

        // Insert into database
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(definition.getTableName()).append(" (");

        List<String> columns = new ArrayList<>(recordData.keySet());
        sql.append(String.join(", ", columns));

        sql.append(") VALUES (");

        List<String> placeholders = Collections.nCopies(columns.size(), "?");
        sql.append(String.join(", ", placeholders));

        sql.append(")");

        Object[] values = columns.stream()
                .map(recordData::get)
                .toArray();

        jdbcTemplate.update(sql.toString(), values);

        // Sync to Elasticsearch
        syncService.syncRecord(collectionName, recordData);

        return recordData;
    }

    /**
     * Validates data against field definitions.
     *
     * @param definition the entity definition
     * @param recordData the data to validate
     */
    private void validateData(EntityDefinition definition, Map<String, Object> recordData) {
        for (FieldDefinition field : definition.getFields()) {
            String fieldName = field.getName();

            // Check required fields
            if (!field.isNullable() && !recordData.containsKey(fieldName)) {
                throw new IllegalArgumentException("Missing required field: " + fieldName);
            }

            // TODO: Add more validation logic
        }
    }

    @Override
    @Transactional
    public Map<String, Object> updateRecord(String collectionName, String id, Map<String, Object> recordData) {
        EntityDefinition definition = getEntityDefinition(collectionName);

        // Ensure ID is included in data
        recordData.put("id", id);

        // Validate data
        validateData(definition, recordData);

        // Update in database
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(definition.getTableName()).append(" SET ");

        List<String> setClauses = recordData.keySet().stream()
                .filter(key -> !key.equals("id"))
                .map(key -> key + " = ?")
                .collect(Collectors.toList());

        sql.append(String.join(", ", setClauses));
        sql.append(", modified_date = CURRENT_TIMESTAMP");
        sql.append(" WHERE id = ?");

        List<Object> values = recordData.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("id"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        values.add(id);

        int updated = jdbcTemplate.update(sql.toString(), values.toArray());

        if (updated == 0) {
            throw new EntityNotFoundException(collectionName, id);
        }

        // Sync to Elasticsearch
        syncService.syncRecord(collectionName, recordData);

        return recordData;
    }

    @Override
    public Optional<Map<String, Object>> getRecord(String collectionName, String id) {
        EntityDefinition definition = getEntityDefinition(collectionName);

        try {
            String sql = "SELECT * FROM " + definition.getTableName() + " WHERE id = ?";

            Map<String, Object> result = jdbcTemplate.queryForObject(
                    sql,
                    mapRowToRecord(),
                    id
            );

            return Optional.ofNullable(result);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Creates a row mapper that maps a database row to a record map.
     *
     * @return the row mapper
     */
    private RowMapper<Map<String, Object>> mapRowToRecord() {
        return (rs, rowNum) -> {
            Map<String, Object> record = new HashMap<>();

            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String columnName = rs.getMetaData().getColumnName(i);
                Object value = rs.getObject(i);
                record.put(columnName, value);
            }

            return record;
        };
    }

    @Override
    @Transactional
    public boolean deleteRecord(String collectionName, String id) {
        EntityDefinition definition = getEntityDefinition(collectionName);

        String sql = "DELETE FROM " + definition.getTableName() + " WHERE id = ?";

        int deleted = jdbcTemplate.update(sql, id);

        if (deleted > 0) {
            // Sync deletion to Elasticsearch
            syncService.deleteRecord(collectionName, id);
            return true;
        }

        return false;
    }

    @Override
    public Page<Map<String, Object>> search(String collectionName, String search, String filter, String sort, Pageable pageable) {
        try {
            EntityDefinition definition = getEntityDefinition(collectionName);

            // Build the search request
            SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
                    .index(definition.getIndexName())
                    .from(pageable.getPageNumber() * pageable.getPageSize())
                    .size(pageable.getPageSize());

            // Build the query
            BoolQuery.Builder queryBuilder = new BoolQuery.Builder();

            // Full-text search
            if (search != null && !search.isEmpty()) {
                queryBuilder.must(
                        Query.of(q -> q
                                .multiMatch(m -> m
                                        .query(search)
                                        .fuzziness("AUTO")
                                )
                        )
                );
            }

            // TODO: Add filter parsing logic

            // TODO: Add sort parsing logic

            SearchRequest searchRequest = searchRequestBuilder
                    .query(q -> q.bool(queryBuilder.build()))
                    .build();

            // Execute the search
            SearchResponse<Map> response = esClient.search(
                    searchRequest,
                    Map.class
            );

            // Convert search hits to records
            List<Map<String, Object>> records = response.hits().hits().stream()
                    .map(hit -> {
                        Map<String, Object> source = hit.source();
                        if (source != null) {
                            // Make sure the ID is included
                            source.put("id", hit.id());
                            return source;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Create the page
            long total = response.hits().total().value();

            return new PageImpl<>(records, pageable, total);
        } catch (IOException e) {
            logger.error("Search error: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    @Override
    public void registerEntityDefinition(EntityDefinition definition) {
        entityDefinitions.put(definition.getName(), definition);
    }

    /**
     * Gets an entity definition by name, throwing an exception if not found.
     *
     * @param collectionName the collection name
     * @return the entity definition
     * @throws EntityNotFoundException if not found
     */
    private EntityDefinition getEntityDefinition(String collectionName) {
        EntityDefinition definition = entityDefinitions.get(collectionName);
        if (definition == null) {
            throw new EntityNotFoundException("Collection not found: " + collectionName);
        }
        return definition;
    }

    /**
     * Entity registrar bean for registering entities via Spring configuration.
     */
    public static class EntityRegistrar {
        private CollectionService collectionService;
        private EntityDefinition entityDefinition;

        public void setCollectionService(CollectionService collectionService) {
            this.collectionService = collectionService;
        }

        public void setEntityDefinition(EntityDefinition entityDefinition) {
            this.entityDefinition = entityDefinition;
        }

        @PostConstruct
        public void registerEntity() {
            if (collectionService != null && entityDefinition != null) {
                collectionService.registerEntityDefinition(entityDefinition);
            }
        }
    }
}