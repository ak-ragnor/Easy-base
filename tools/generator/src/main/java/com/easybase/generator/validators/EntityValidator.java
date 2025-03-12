package com.easybase.generator.validators;

import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.model.FieldDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates an entity definition for correctness.
 * This class performs validation on an EntityDefinition to ensure
 * it meets all requirements for code generation.
 */
public class EntityValidator {

    private FieldValidator fieldValidator;

    /**
     * Constructs a new EntityValidator with a FieldValidator.
     */
    public EntityValidator() {
        this.fieldValidator = new FieldValidator();
    }

    /**
     * Validates an entity definition.
     *
     * @param entity The entity definition to validate
     * @return The validation result
     */
    public ValidationResult validate(EntityDefinition entity) {
        List<String> errors = new ArrayList<>();

        // Validate required fields
        if (entity.getEntity() == null || entity.getEntity().isEmpty()) {
            errors.add("Entity name is required");
        }

        if (entity.getPackageName() == null || entity.getPackageName().isEmpty()) {
            errors.add("Package name is required");
        }

        // Validate field uniqueness
        Set<String> fieldNames = new HashSet<>();
        for (FieldDefinition field : entity.getFields()) {
            if (fieldNames.contains(field.getName())) {
                errors.add("Duplicate field name: " + field.getName());
            }
            fieldNames.add(field.getName());

            // Validate individual field
            ValidationResult fieldResult = fieldValidator.validate(field);
            errors.addAll(fieldResult.getErrors());
        }

        // Validate primary key existence
        boolean hasPrimaryKey = entity.getFields().stream()
                .anyMatch(FieldDefinition::isPrimaryKey);

        if (!hasPrimaryKey) {
            errors.add("Entity must have a primary key field");
        }

        return new ValidationResult(errors);
    }
}