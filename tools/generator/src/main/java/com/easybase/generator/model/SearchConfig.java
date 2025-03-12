package com.easybase.generator.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the search configuration for a field.
 * This defines how the field is indexed in Elasticsearch.
 */
public class SearchConfig {
    private String type;
    private String analyzer;
    private Map<String, Object> fields = new HashMap<>();
    private Map<String, Object> properties = new HashMap<>();

    // Constructors
    public SearchConfig() {
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Generates the Elasticsearch mapping for this search configuration.
     *
     * @return A map representing the Elasticsearch mapping
     */
    public Map<String, Object> generateMapping() {
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("type", type);

        if (analyzer != null) {
            mapping.put("analyzer", analyzer);
        }

        if (!fields.isEmpty()) {
            mapping.put("fields", fields);
        }

        if (!properties.isEmpty()) {
            mapping.put("properties", properties);
        }

        return mapping;
    }
}