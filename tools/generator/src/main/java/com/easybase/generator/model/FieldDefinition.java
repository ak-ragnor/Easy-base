package com.easybase.generator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a field definition within an entity.
 */
public class FieldDefinition {
    private String name;
    private String type;
    private boolean primaryKey = false;
    private boolean nullable = true;
    private boolean unique = false;
    private Integer length;
    private Object defaultValue;
    private String description;
    private List<ValidationRule> validations = new ArrayList<>();
    private SearchMapping searchMapping;
    private boolean generated = false;

    // For enum types
    private String enumClass;
    private List<String> enumValues = new ArrayList<>();

    // Builder pattern
    public static class Builder {
        private FieldDefinition field = new FieldDefinition();

        public Builder withName(String name) {
            field.name = name;
            return this;
        }

        public Builder withType(String type) {
            field.type = type;
            return this;
        }

        public Builder withPrimaryKey(boolean primaryKey) {
            field.primaryKey = primaryKey;
            return this;
        }

        public Builder withNullable(boolean nullable) {
            field.nullable = nullable;
            return this;
        }

        public Builder withUnique(boolean unique) {
            field.unique = unique;
            return this;
        }

        public Builder withLength(Integer length) {
            field.length = length;
            return this;
        }

        public Builder withDefaultValue(Object defaultValue) {
            field.defaultValue = defaultValue;
            return this;
        }

        public Builder withDescription(String description) {
            field.description = description;
            return this;
        }

        public Builder withValidation(ValidationRule validation) {
            field.validations.add(validation);
            return this;
        }

        public Builder withValidations(List<ValidationRule> validations) {
            field.validations.addAll(validations);
            return this;
        }

        public Builder withSearchMapping(SearchMapping searchMapping) {
            field.searchMapping = searchMapping;
            return this;
        }

        public Builder withGenerated(boolean generated) {
            field.generated = generated;
            return this;
        }

        public Builder withEnumClass(String enumClass) {
            field.enumClass = enumClass;
            return this;
        }

        public Builder withEnumValues(List<String> enumValues) {
            field.enumValues.addAll(enumValues);
            return this;
        }

        public FieldDefinition build() {
            return field;
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

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ValidationRule> getValidations() {
        return validations;
    }

    public void setValidations(List<ValidationRule> validations) {
        this.validations = validations;
    }

    public SearchMapping getSearchMapping() {
        return searchMapping;
    }

    public void setSearchMapping(SearchMapping searchMapping) {
        this.searchMapping = searchMapping;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public String getEnumClass() {
        return enumClass;
    }

    public void setEnumClass(String enumClass) {
        this.enumClass = enumClass;
    }

    public List<String> getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
    }

    // Utility methods

    /**
     * Checks if this field is an enum type.
     */
    public boolean isEnum() {
        return "Enum".equals(type);
    }

    /**
     * Gets the Java type for this field.
     */
    public String getJavaType() {
        if (isEnum()) {
            return enumClass;
        }

        switch (type) {
            case "String":
                return "String";
            case "Integer":
            case "Int":
                return "Integer";
            case "Long":
                return "Long";
            case "Double":
                return "Double";
            case "Float":
                return "Float";
            case "Boolean":
                return "Boolean";
            case "Date":
                return "java.time.LocalDate";
            case "DateTime":
                return "java.time.LocalDateTime";
            case "Instant":
                return "java.time.Instant";
            case "BigDecimal":
                return "java.math.BigDecimal";
            case "UUID":
                return "java.util.UUID";
            default:
                return type;
        }
    }

    /**
     * Gets the database column definition for this field.
     */
    public String getColumnDefinition() {
        StringBuilder sb = new StringBuilder();

        // Column type
        if (isEnum()) {
            sb.append("VARCHAR");
            if (length != null) {
                sb.append("(").append(length).append(")");
            } else {
                sb.append("(50)"); // Default length for enum
            }
        } else {
            switch (type) {
                case "String":
                    sb.append("VARCHAR");
                    if (length != null) {
                        sb.append("(").append(length).append(")");
                    } else {
                        sb.append("(255)");
                    }
                    break;
                case "Integer":
                case "Int":
                    sb.append("INTEGER");
                    break;
                case "Long":
                    sb.append("BIGINT");
                    break;
                case "Double":
                    sb.append("DOUBLE PRECISION");
                    break;
                case "Float":
                    sb.append("FLOAT");
                    break;
                case "Boolean":
                    sb.append("BOOLEAN");
                    break;
                case "Date":
                    sb.append("DATE");
                    break;
                case "DateTime":
                case "Instant":
                    sb.append("TIMESTAMP");
                    break;
                case "BigDecimal":
                    sb.append("DECIMAL(19, 2)");
                    break;
                case "UUID":
                    sb.append("VARCHAR(36)");
                    break;
                default:
                    sb.append("VARCHAR(255)");
            }
        }

        // Nullable constraint
        if (!nullable) {
            sb.append(" NOT NULL");
        }

        // Unique constraint
        if (unique) {
            sb.append(" UNIQUE");
        }

        // Default value
        if (defaultValue != null) {
            sb.append(" DEFAULT ");
            if (defaultValue instanceof String) {
                sb.append("'").append(defaultValue).append("'");
            } else if (defaultValue instanceof Boolean) {
                sb.append((Boolean) defaultValue ? "TRUE" : "FALSE");
            } else {
                sb.append(defaultValue);
            }
        }

        return sb.toString();
    }

    /**
     * Gets JPA column annotations for this field.
     */
    public List<String> getJpaAnnotations() {
        List<String> annotations = new ArrayList<>();

        // Primary key annotation
        if (primaryKey) {
            annotations.add("@Id");
            if (generated) {
                annotations.add("@GeneratedValue(strategy = GenerationType.IDENTITY)");
            }
        }

        // Column annotation
        StringBuilder columnAnnotation = new StringBuilder("@Column(name = \"");
        columnAnnotation.append(name).append("\"");

        if (!nullable) {
            columnAnnotation.append(", nullable = false");
        }

        if (unique) {
            columnAnnotation.append(", unique = true");
        }

        if (length != null && "String".equals(type)) {
            columnAnnotation.append(", length = ").append(length);
        }

        columnAnnotation.append(")");
        annotations.add(columnAnnotation.toString());

        // Enum annotation
        if (isEnum()) {
            annotations.add("@Enumerated(EnumType.STRING)");
        }

        return annotations;
    }

    /**
     * Gets all validation annotations for this field.
     */
    public List<String> getValidationAnnotations() {
        List<String> annotations = new ArrayList<>();

        for (ValidationRule validation : validations) {
            annotations.add(validation.getAnnotation());
        }

        return annotations;
    }

    /**
     * Gets all imports required for this field.
     */
    public List<String> getRequiredImports() {
        List<String> imports = new ArrayList<>();

        // Add imports based on type
        switch (type) {
            case "Date":
                imports.add("java.time.LocalDate");
                break;
            case "DateTime":
                imports.add("java.time.LocalDateTime");
                break;
            case "Instant":
                imports.add("java.time.Instant");
                break;
            case "BigDecimal":
                imports.add("java.math.BigDecimal");
                break;
            case "UUID":
                imports.add("java.util.UUID");
                break;
        }

        // Add imports for validations
        for (ValidationRule validation : validations) {
            imports.add(validation.getImport());
        }

        return imports;
    }
}