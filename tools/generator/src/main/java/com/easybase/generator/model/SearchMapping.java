package com.easybase.generator.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an Elasticsearch mapping for a field.
 */
public class SearchMapping {
    private String type;
    private String analyzer;
    private Map<String, Object> fields = new HashMap<>();
    private Map<String, Object> properties = new HashMap<>();

    // Builder pattern
    public static class Builder {
        private SearchMapping mapping = new SearchMapping();

        public Builder withType(String type) {
            mapping.type = type;
            return this;
        }

        public Builder withAnalyzer(String analyzer) {
            mapping.analyzer = analyzer;
            return this;
        }

        public Builder withField(String name, Object value) {
            mapping.fields.put(name, value);
            return this;
        }

        public Builder withFields(Map<String, Object> fields) {
            mapping.fields.putAll(fields);
            return this;
        }

        public Builder withProperty(String name, Object value) {
            mapping.properties.put(name, value);
            return this;
        }

        public Builder withProperties(Map<String, Object> properties) {
            mapping.properties.putAll(properties);
            return this;
        }

        public SearchMapping build() {
            return mapping;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters

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
     * Gets the Elasticsearch mapping as a map.
     */
    public Map<String, Object> toMap() {
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