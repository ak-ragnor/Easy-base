package com.easybase.core.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Definition of an entity (collection) in the system.
 * This class defines the schema of a collection, including its name,
 * database table name, and field definitions.
 */
public class EntityDefinition {
    private String name;
    private String tableName;
    private List<FieldDefinition> fields = new ArrayList<>();
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * Default constructor.
     */
    public EntityDefinition() {
    }

    /**
     * Constructor with name.
     *
     * @param name the entity name
     */
    public EntityDefinition(String name) {
        this.name = name;
        this.tableName = name.toLowerCase();
    }

    /**
     * Constructor with name and table name.
     *
     * @param name the entity name
     * @param tableName the database table name
     */
    public EntityDefinition(String name, String tableName) {
        this.name = name;
        this.tableName = tableName;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public void setFields(List<FieldDefinition> fields) {
        this.fields = fields;
    }

    /**
     * Adds a field to this entity definition.
     *
     * @param field the field to add
     */
    public void addField(FieldDefinition field) {
        this.fields.add(field);
    }

    /**
     * Gets a field by name.
     *
     * @param fieldName the field name
     * @return the field, if found
     */
    public FieldDefinition getField(String fieldName) {
        return fields.stream()
                .filter(f -> f.getName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Sets a metadata value.
     *
     * @param key the metadata key
     * @param value the metadata value
     */
    public void setMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    /**
     * Gets a metadata value.
     *
     * @param key the metadata key
     * @return the metadata value, or null if not found
     */
    public Object getMetadata(String key) {
        return this.metadata.get(key);
    }

    /**
     * Gets the Elasticsearch index name for this entity.
     *
     * @return the Elasticsearch index name
     */
    public String getIndexName() {
        return tableName;
    }
}

