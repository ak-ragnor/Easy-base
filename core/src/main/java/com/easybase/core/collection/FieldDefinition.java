package com.easybase.core.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * Definition of a field in an entity.
 * This class defines the schema of a field, including its name,
 * type, and various properties such as whether it's a primary key.
 */
public class FieldDefinition {
    private String name;
    private String type;
    private boolean primaryKey = false;
    private boolean nullable = true;
    private Integer length;
    private Object defaultValue;
    private Map<String, Object> searchMapping = new HashMap<>();

    /**
     * Default constructor.
     */
    public FieldDefinition() {
    }

    /**
     * Constructor with name and type.
     *
     * @param name the field name
     * @param type the field type
     */
    public FieldDefinition(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Constructor with name, type, and primary key flag.
     *
     * @param name the field name
     * @param type the field type
     * @param primaryKey whether this field is a primary key
     */
    public FieldDefinition(String name, String type, boolean primaryKey) {
        this.name = name;
        this.type = type;
        this.primaryKey = primaryKey;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Map<String, Object> getSearchMapping() {
        return searchMapping;
    }

    public void setSearchMapping(Map<String, Object> searchMapping) {
        this.searchMapping = searchMapping;
    }

    /**
     * Gets the SQL column definition for this field.
     *
     * @return the SQL column definition
     */
    public String getSqlDefinition() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ");

        // Map field type to SQL type
        switch (type.toLowerCase()) {
            case "string":
                sb.append("VARCHAR");
                if (length != null) {
                    sb.append("(").append(length).append(")");
                } else {
                    sb.append("(255)");
                }
                break;
            case "integer":
            case "int":
                sb.append("INTEGER");
                break;
            case "long":
                sb.append("BIGINT");
                break;
            case "double":
                sb.append("DOUBLE PRECISION");
                break;
            case "float":
                sb.append("FLOAT");
                break;
            case "boolean":
                sb.append("BOOLEAN");
                break;
            case "date":
                sb.append("DATE");
                break;
            case "datetime":
                sb.append("TIMESTAMP");
                break;
            case "uuid":
                sb.append("VARCHAR(36)");
                break;
            default:
                sb.append("VARCHAR(255)");
        }

        // Add constraints
        if (!nullable) {
            sb.append(" NOT NULL");
        }

        return sb.toString();
    }

    /**
     * Gets the Elasticsearch mapping for this field.
     *
     * @return the Elasticsearch mapping
     */
    public Map<String, Object> getElasticsearchMapping() {
        Map<String, Object> mapping = new HashMap<>();
        mapping.put(name, searchMapping);
        return mapping;
    }
}
