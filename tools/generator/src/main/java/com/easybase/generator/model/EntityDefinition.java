package com.easybase.generator.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the complete definition of an entity as parsed from YAML.
 * This class serves as the primary model for code generation, containing
 * all information needed to generate the entity's code components.
 */
public class EntityDefinition {
    private String module;
    private String entity;
    private String table;
    private String packageName;
    private boolean generateController = true;
    private List<FieldDefinition> fields = new ArrayList<>();
    private List<FinderDefinition> finders = new ArrayList<>();

    // Constructor
    public EntityDefinition() {
    }

    // Getters and Setters
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
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

    public boolean isGenerateController() {
        return generateController;
    }

    public void setGenerateController(boolean generateController) {
        this.generateController = generateController;
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

    public List<FinderDefinition> getFinders() {
        return finders;
    }

    public void setFinders(List<FinderDefinition> finders) {
        this.finders = finders;
    }

    /**
     * Gets a list of all imports needed for this entity.
     *
     * @return A list of fully qualified class names to import
     */
    public List<String> getRequiredImports() {
        List<String> imports = new ArrayList<>();

        // Add standard imports
        imports.add("java.util.UUID");
        imports.add("java.time.Instant");

        // Add imports based on field types
        for (FieldDefinition field : fields) {
            List<String> fieldImports = field.getRequiredImports();
            for (String fieldImport : fieldImports) {
                if (!imports.contains(fieldImport)) {
                    imports.add(fieldImport);
                }
            }

            // Add imports for relationship targets with package specified
            if (field.isRelationship() && field.getTargetPackage() != null && !field.getTargetPackage().isEmpty()) {
                // Import the target entity
                String targetImport = field.getTargetPackage() + "." + field.getTarget();
                if (!imports.contains(targetImport)) {
                    imports.add(targetImport);
                }
            }
        }

        return imports;
    }

    /**
     * Gets the primary key field for this entity.
     *
     * @return The field definition representing the primary key
     */
    public FieldDefinition getPrimaryKeyField() {
        for (FieldDefinition field : fields) {
            if (field.isPrimaryKey()) {
                return field;
            }
        }
        // Default to ID if no primary key specified
        FieldDefinition defaultId = new FieldDefinition();
        defaultId.setName("id");
        defaultId.setType("UUID");
        defaultId.setPrimaryKey(true);
        defaultId.setGenerated(true);
        return defaultId;
    }

    /**
     * Checks if the entity has audit fields (createdDate, lastModifiedDate).
     *
     * @return True if audit fields exist
     */
    public boolean hasAuditFields() {
        for (FieldDefinition field : fields) {
            if (field.getName().equals("createdDate") ||
                    field.getName().equals("lastModifiedDate")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all fields that need to be indexed based on finder methods.
     *
     * @return A set of field names that should be indexed
     */
    public Set<String> getFieldsUsedInFinders() {
        Set<String> indexedFields = new HashSet<>();

        for (FinderDefinition finder : finders) {
            // Extract field names from the finder method name
            String methodName = finder.getName();
            if (methodName.startsWith("findBy") || methodName.startsWith("countBy")) {
                String[] parts = methodName.replaceFirst("^(findBy|countBy)", "").split("And|Or");
                for (String part : parts) {
                    // Convert to field name (camelCase)
                    String fieldName = part;
                    if (!fieldName.isEmpty()) {
                        fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
                    }

                    // Add field that matches this name (ignoring suffixes like 'Contains', 'GreaterThan', etc.)
                    for (FieldDefinition field : fields) {
                        if (fieldName.startsWith(field.getName())) {
                            indexedFields.add(field.getName());
                        }
                    }
                }
            }
        }

        return indexedFields;
    }

    /**
     * Determines if this entity has any relationship fields.
     *
     * @return True if this entity has at least one relationship field
     */
    public boolean hasRelationships() {
        for (FieldDefinition field : fields) {
            if (field.isRelationship()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a list of all related modules that this entity depends on.
     *
     * @return A list of module names
     */
    public List<String> getRelatedModules() {
        List<String> modules = new ArrayList<>();

        for (FieldDefinition field : fields) {
            if (field.isRelationship() && field.getTargetModule() != null) {
                if (!modules.contains(field.getTargetModule())) {
                    modules.add(field.getTargetModule());
                }
            }
        }

        return modules;
    }

}