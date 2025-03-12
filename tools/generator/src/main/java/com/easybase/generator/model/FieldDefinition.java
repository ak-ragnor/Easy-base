package com.easybase.generator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a field within an entity definition.
 * Fields define the structure of the entity and can be simple property types
 * or complex relationship types.
 */
public class FieldDefinition {
    private String name;
    private String type;
    private boolean nullable = true;
    private boolean primaryKey = false;
    private boolean generated = false;
    private boolean indexed = false;
    private Integer length;
    private String description;
    private String defaultValue;
    private List<Map<String, Object>> validation = new ArrayList<>();
    private SearchConfig search;

    // Relationship specific properties
    private String target;
    private String targetPackage;
    private String targetModule;
    private String relationType;
    private String joinColumn;

    // Enum specific properties
    private String enumClass;
    private List<String> values = new ArrayList<>();

    // Constructors
    public FieldDefinition() {
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

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<Map<String, Object>> getValidation() {
        return validation;
    }

    public void setValidation(List<Map<String, Object>> validation) {
        this.validation = validation;
    }

    public SearchConfig getSearch() {
        return search;
    }

    public void setSearch(SearchConfig search) {
        this.search = search;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public String getTargetModule() {
        return targetModule;
    }

    public void setTargetModule(String targetModule) {
        this.targetModule = targetModule;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public void setJoinColumn(String joinColumn) {
        this.joinColumn = joinColumn;
    }

    public String getEnumClass() {
        return enumClass;
    }

    public void setEnumClass(String enumClass) {
        this.enumClass = enumClass;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    /**
     * Determines if this field represents a relationship.
     *
     * @return True if this is a relationship field
     */
    public boolean isRelationship() {
        return "Relationship".equals(type);
    }

    /**
     * Determines if this field represents an enumeration.
     *
     * @return True if this is an enum field
     */
    public boolean isEnum() {
        return "Enum".equals(type);
    }

    /**
     * Gets the Java type for this field.
     * Maps custom types (like "Relationship") to appropriate Java types.
     *
     * @return The Java type as a string
     */
    public String getJavaType() {
        if (isRelationship()) {
            return target;
        } else if (isEnum()) {
            return enumClass;
        }
        return type;
    }

    /**
     * Gets a list of all imports needed for this field.
     *
     * @return A list of fully qualified class names to import
     */
    public List<String> getRequiredImports() {
        List<String> imports = new ArrayList<>();

        // Add imports based on field type
        if ("Date".equals(type) || "DateTime".equals(type)) {
            imports.add("java.time.Instant");
        } else if ("BigDecimal".equals(type)) {
            imports.add("java.math.BigDecimal");
        }

        // Add validation-related imports
        for (Map<String, Object> validationRule : validation) {
            String validationType = (String) validationRule.get("type");
            if ("Email".equals(validationType)) {
                imports.add("jakarta.validation.constraints.Email");
            } else if ("NotBlank".equals(validationType)) {
                imports.add("jakarta.validation.constraints.NotBlank");
            } else if ("NotNull".equals(validationType)) {
                imports.add("jakarta.validation.constraints.NotNull");
            } else if ("Size".equals(validationType)) {
                imports.add("jakarta.validation.constraints.Size");
            } else if ("Pattern".equals(validationType)) {
                imports.add("jakarta.validation.constraints.Pattern");
            }
        }

        return imports;
    }

    /**
     * Gets the JPA column definition for this field.
     *
     * @return A string representing the SQL column type
     */
    public String getColumnDefinition() {
        StringBuilder column = new StringBuilder();

        // Handle different field types
        if ("String".equals(type)) {
            column.append("VARCHAR");
            if (length != null) {
                column.append("(").append(length).append(")");
            } else {
                column.append("(255)");
            }
        } else if ("Integer".equals(type) || "int".equals(type)) {
            column.append("INTEGER");
        } else if ("Long".equals(type) || "long".equals(type)) {
            column.append("BIGINT");
        } else if ("Double".equals(type) || "double".equals(type)) {
            column.append("DOUBLE PRECISION");
        } else if ("Float".equals(type) || "float".equals(type)) {
            column.append("FLOAT");
        } else if ("Boolean".equals(type) || "boolean".equals(type)) {
            column.append("BOOLEAN");
        } else if ("Date".equals(type)) {
            column.append("DATE");
        } else if ("DateTime".equals(type) || "Instant".equals(type)) {
            column.append("TIMESTAMP");
        } else if ("BigDecimal".equals(type)) {
            column.append("DECIMAL(19, 2)");
        } else if ("UUID".equals(type)) {
            column.append("VARCHAR(36)");
        } else if (isEnum()) {
            column.append("VARCHAR(50)");
        }

        // Add NOT NULL constraint if needed
        if (!nullable) {
            column.append(" NOT NULL");
        }

        return column.toString();
    }

    /**
     * Determines if this relationship field references an entity in a different module.
     *
     * @param currentPackage The current entity's package
     * @return True if the target entity is in a different module
     */
    public boolean isCrossModuleRelationship(String currentPackage) {
        if (!isRelationship()) {
            return false;
        }

        // For now, we'll just assume all relationships are cross-module
        // In a more advanced implementation, we could check entity registry or
        // define relationship targets with their full package
        return true;
    }

    /**
     * Gets the fully qualified name of the target class for relationships.
     */
    public String getFullyQualifiedTarget() {
        if (!isRelationship() || targetPackage == null || targetPackage.isEmpty()) {
            return target;
        }
        return targetPackage + "." + target;
    }

}