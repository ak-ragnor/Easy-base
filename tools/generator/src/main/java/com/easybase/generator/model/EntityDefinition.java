package com.easybase.generator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents an entity definition from a YAML configuration.
 * Central model object for the code generation process.
 */
public class EntityDefinition {
    private String name;
    private String table;
    private String packageName;
    private List<FieldDefinition> fields = new ArrayList<>();
    private List<RelationshipDefinition> relationships = new ArrayList<>();
    private List<FinderDefinition> finders = new ArrayList<>();
    private List<DtoLevel> dtoLevels = new ArrayList<>();
    private List<ListenerDefinition> listeners = new ArrayList<>();
    private Map<String, Object> options = new HashMap<>();
    private AuditConfig auditConfig = new AuditConfig();

    // Builder pattern for constructing entity definitions
    public static class Builder {
        private EntityDefinition entity = new EntityDefinition();

        public Builder withName(String name) {
            entity.name = name;
            return this;
        }

        public Builder withTable(String table) {
            entity.table = table;
            return this;
        }

        public Builder withPackage(String packageName) {
            entity.packageName = packageName;
            return this;
        }

        public Builder withField(FieldDefinition field) {
            entity.fields.add(field);
            return this;
        }

        public Builder withFields(List<FieldDefinition> fields) {
            entity.fields.addAll(fields);
            return this;
        }

        public Builder withRelationship(RelationshipDefinition relationship) {
            entity.relationships.add(relationship);
            return this;
        }

        public Builder withRelationships(List<RelationshipDefinition> relationships) {
            entity.relationships.addAll(relationships);
            return this;
        }

        public Builder withFinder(FinderDefinition finder) {
            entity.finders.add(finder);
            return this;
        }

        public Builder withFinders(List<FinderDefinition> finders) {
            entity.finders.addAll(finders);
            return this;
        }

        public Builder withDtoLevel(DtoLevel dtoLevel) {
            entity.dtoLevels.add(dtoLevel);
            return this;
        }

        public Builder withDtoLevels(List<DtoLevel> dtoLevels) {
            entity.dtoLevels.addAll(dtoLevels);
            return this;
        }

        public Builder withListener(ListenerDefinition listener) {
            entity.listeners.add(listener);
            return this;
        }

        public Builder withListeners(List<ListenerDefinition> listeners) {
            entity.listeners.addAll(listeners);
            return this;
        }

        public Builder withOption(String key, Object value) {
            entity.options.put(key, value);
            return this;
        }

        public Builder withOptions(Map<String, Object> options) {
            entity.options.putAll(options);
            return this;
        }

        public Builder withAuditConfig(AuditConfig auditConfig) {
            entity.auditConfig = auditConfig;
            return this;
        }

        public EntityDefinition build() {
            return entity;
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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public void setFields(List<FieldDefinition> fields) {
        this.fields = fields;
    }

    public List<RelationshipDefinition> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<RelationshipDefinition> relationships) {
        this.relationships = relationships;
    }

    public List<FinderDefinition> getFinders() {
        return finders;
    }

    public void setFinders(List<FinderDefinition> finders) {
        this.finders = finders;
    }

    public List<DtoLevel> getDtoLevels() {
        return dtoLevels;
    }

    public void setDtoLevels(List<DtoLevel> dtoLevels) {
        this.dtoLevels = dtoLevels;
    }

    public List<ListenerDefinition> getListeners() {
        return listeners;
    }

    public void setListeners(List<ListenerDefinition> listeners) {
        this.listeners = listeners;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public AuditConfig getAuditConfig() {
        return auditConfig;
    }

    public void setAuditConfig(AuditConfig auditConfig) {
        this.auditConfig = auditConfig;
    }

    // Utility methods

    /**
     * Gets the primary key field of this entity.
     */
    public Optional<FieldDefinition> getPrimaryKey() {
        return fields.stream()
                .filter(FieldDefinition::isPrimaryKey)
                .findFirst();
    }

    /**
     * Gets all searchable fields.
     */
    public List<FieldDefinition> getSearchableFields() {
        return fields.stream()
                .filter(f -> f.getSearchMapping() != null)
                .collect(Collectors.toList());
    }

    /**
     * Checks if soft delete is enabled.
     */
    public boolean isSoftDeleteEnabled() {
        Object softDelete = options.get("softDelete");
        return softDelete != null && (Boolean) softDelete;
    }

    /**
     * Gets the soft delete field name.
     */
    public String getSoftDeleteField() {
        return (String) options.getOrDefault("softDeleteField", "deleted");
    }

    /**
     * Gets the default package path for a specific component type.
     *
     * @param componentType The component type (e.g., "model", "service", "repository")
     * @return The package path
     */
    public String getComponentPackage(String componentType) {
        return packageName + "." + componentType;
    }

    /**
     * Gets the base package path for a specific component type.
     *
     * @param componentType The component type (e.g., "model", "service", "repository")
     * @return The base package path
     */
    public String getBaseComponentPackage(String componentType) {
        return getComponentPackage(componentType) + ".base";
    }

    /**
     * Alias for getComponentPackage to support FreeMarker template calls.
     *
     * @param componentType The component type (e.g., "model", "service", "repository")
     * @return The package path
     */
    public String componentPackage(String componentType) {
        return getComponentPackage(componentType);
    }

    /**
     * Alias for getBaseComponentPackage to support FreeMarker template calls.
     *
     * @param componentType The component type (e.g., "model", "service", "repository")
     * @return The base package path
     */
    public String baseComponentPackage(String componentType) {
        return getBaseComponentPackage(componentType);
    }

    /**
     * Gets the full table name, including prefix if specified.
     */
    public String getFullTableName() {
        return table;
    }

    /**
     * Gets all import statements needed for this entity definition.
     */
    public List<String> getRequiredImports() {
        List<String> imports = new ArrayList<>();

        // Add imports based on field types
        fields.forEach(field -> imports.addAll(field.getRequiredImports()));

        // Add imports for relationships
        relationships.forEach(rel -> imports.addAll(rel.getRequiredImports()));

        return imports.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Audit configuration class.
     */
    public static class AuditConfig {
        private boolean enabled = false;
        private List<String> fields = new ArrayList<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getFields() {
            return fields;
        }

        public void setFields(List<String> fields) {
            this.fields = fields;
        }

        public boolean hasField(String fieldName) {
            return fields.contains(fieldName);
        }
    }
}