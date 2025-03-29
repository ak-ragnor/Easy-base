package com.easybase.generator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a DTO projection level.
 */
public class DtoLevel {
    private String name;
    private List<String> fields = new ArrayList<>();

    // Builder pattern
    public static class Builder {
        private DtoLevel dtoLevel = new DtoLevel();

        public Builder withName(String name) {
            dtoLevel.name = name;
            return this;
        }

        public Builder withField(String field) {
            dtoLevel.fields.add(field);
            return this;
        }

        public Builder withFields(List<String> fields) {
            dtoLevel.fields.addAll(fields);
            return this;
        }

        public DtoLevel build() {
            return dtoLevel;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    /**
     * Checks if this DTO level includes a specific field.
     *
     * @param fieldName The field name
     * @return True if the field is included, false otherwise
     */
    public boolean includesField(String fieldName) {
        return fields.contains(fieldName);
    }

    /**
     * Gets the DTO class name for this level.
     *
     * @param entityName The entity name
     * @return The DTO class name
     */
    public String getDtoClassName(String entityName) {
        if (name.equals("Basic")) {
            return entityName + "DTO";
        } else {
            return entityName + name + "DTO";
        }
    }
}