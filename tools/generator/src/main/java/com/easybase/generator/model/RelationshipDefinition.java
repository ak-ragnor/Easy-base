package com.easybase.generator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a relationship between entities.
 */
public class RelationshipDefinition {
    // Relationship types
    public enum RelationType {
        ONE_TO_ONE,
        ONE_TO_MANY,
        MANY_TO_ONE,
        MANY_TO_MANY
    }

    private String fromEntity;
    private String toEntity;
    private RelationType type;
    private String mappedBy;
    private String joinColumn;
    private String joinTable;
    private boolean nullable = true;
    private boolean fetchEager = false;
    private String targetPackage;

    // Builder pattern
    public static class Builder {
        private RelationshipDefinition relationship = new RelationshipDefinition();

        public Builder withFromEntity(String fromEntity) {
            relationship.fromEntity = fromEntity;
            return this;
        }

        public Builder withToEntity(String toEntity) {
            relationship.toEntity = toEntity;
            return this;
        }

        public Builder withType(RelationType type) {
            relationship.type = type;
            return this;
        }

        public Builder withMappedBy(String mappedBy) {
            relationship.mappedBy = mappedBy;
            return this;
        }

        public Builder withJoinColumn(String joinColumn) {
            relationship.joinColumn = joinColumn;
            return this;
        }

        public Builder withJoinTable(String joinTable) {
            relationship.joinTable = joinTable;
            return this;
        }

        public Builder withNullable(boolean nullable) {
            relationship.nullable = nullable;
            return this;
        }

        public Builder withFetchEager(boolean fetchEager) {
            relationship.fetchEager = fetchEager;
            return this;
        }

        public Builder withTargetPackage(String targetPackage) {
            relationship.targetPackage = targetPackage;
            return this;
        }

        public RelationshipDefinition build() {
            return relationship;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters

    public String getFromEntity() {
        return fromEntity;
    }

    public void setFromEntity(String fromEntity) {
        this.fromEntity = fromEntity;
    }

    public String getToEntity() {
        return toEntity;
    }

    public void setToEntity(String toEntity) {
        this.toEntity = toEntity;
    }

    public RelationType getType() {
        return type;
    }

    public void setType(RelationType type) {
        this.type = type;
    }

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public void setJoinColumn(String joinColumn) {
        this.joinColumn = joinColumn;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(String joinTable) {
        this.joinTable = joinTable;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isFetchEager() {
        return fetchEager;
    }

    public void setFetchEager(boolean fetchEager) {
        this.fetchEager = fetchEager;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    // Utility methods

    /**
     * Gets JPA annotations for this relationship.
     */
    public List<String> getJpaAnnotations() {
        List<String> annotations = new ArrayList<>();

        // Relationship annotation
        switch (type) {
            case ONE_TO_ONE:
                annotations.add("@OneToOne" + getFetchTypeAnnotation());
                break;
            case ONE_TO_MANY:
                annotations.add("@OneToMany" + getFetchTypeAnnotation());
                break;
            case MANY_TO_ONE:
                annotations.add("@ManyToOne" + getFetchTypeAnnotation());
                break;
            case MANY_TO_MANY:
                annotations.add("@ManyToMany" + getFetchTypeAnnotation());
                break;
        }

        // MappedBy annotation
        if (mappedBy != null && !mappedBy.isEmpty()) {
            annotations.add("@MappedBy(\"" + mappedBy + "\")");
        }

        // JoinColumn annotation
        if (joinColumn != null && !joinColumn.isEmpty()) {
            annotations.add("@JoinColumn(name = \"" + joinColumn + "\", nullable = " + nullable + ")");
        }

        // JoinTable annotation
        if (joinTable != null && !joinTable.isEmpty()) {
            annotations.add("@JoinTable(name = \"" + joinTable + "\")");
        }

        return annotations;
    }

    /**
     * Gets all imports required for this relationship.
     */
    public List<String> getRequiredImports() {
        List<String> imports = new ArrayList<>();

        // Add import for target entity
        if (targetPackage != null) {
            imports.add(targetPackage + "." + toEntity);
        }

        // Add JPA annotations imports
        imports.add("jakarta.persistence." + type.name());

        if (joinColumn != null) {
            imports.add("jakarta.persistence.JoinColumn");
        }

        if (joinTable != null) {
            imports.add("jakarta.persistence.JoinTable");
        }

        if (mappedBy != null) {
            imports.add("jakarta.persistence.MappedBy");
        }

        if (fetchEager) {
            imports.add("jakarta.persistence.FetchType");
        }

        // For collections
        if (type == RelationType.ONE_TO_MANY || type == RelationType.MANY_TO_MANY) {
            imports.add("java.util.List");
            imports.add("java.util.ArrayList");
        }

        return imports;
    }

    private String getFetchTypeAnnotation() {
        return fetchEager ? "(fetch = FetchType.EAGER)" : "";
    }

    /**
     * Gets the Java field type for this relationship.
     */
    public String getJavaType() {
        switch (type) {
            case ONE_TO_MANY:
            case MANY_TO_MANY:
                return "List<" + toEntity + ">";
            case ONE_TO_ONE:
            case MANY_TO_ONE:
                return toEntity;
            default:
                return toEntity;
        }
    }

    /**
     * Gets the Java field initialization for this relationship.
     */
    public String getInitialization() {
        switch (type) {
            case ONE_TO_MANY:
            case MANY_TO_MANY:
                return "new ArrayList<>()";
            default:
                return "null";
        }
    }
}