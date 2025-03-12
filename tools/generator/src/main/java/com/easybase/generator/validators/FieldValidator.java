package com.easybase.generator.validators;

import com.easybase.generator.model.FieldDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validates field definitions for correctness.
 * This class performs validation on FieldDefinition objects to ensure
 * they meet all requirements for code generation.
 */
public class FieldValidator {

    // List of valid field types
    private static final List<String> VALID_TYPES = Arrays.asList(
            "String", "Integer", "Long", "Double", "Float", "Boolean",
            "Date", "DateTime", "BigDecimal", "UUID", "Enum", "Relationship"
    );

    // List of valid relationship types
    private static final List<String> VALID_RELATIONSHIP_TYPES = Arrays.asList(
            "OneToOne", "OneToMany", "ManyToOne", "ManyToMany"
    );

    /**
     * Validates a field definition.
     *
     * @param field The field definition to validate
     * @return The validation result
     */
    public ValidationResult validate(FieldDefinition field) {
        List<String> errors = new ArrayList<>();

        // Validate required properties
        if (field.getName() == null || field.getName().isEmpty()) {
            errors.add("Field name is required");
        }

        if (field.getType() == null || field.getType().isEmpty()) {
            errors.add("Field type is required");
        } else if (!VALID_TYPES.contains(field.getType())) {
            errors.add("Invalid field type: " + field.getType());
        }

        // Validate relationship fields
        if ("Relationship".equals(field.getType())) {
            if (field.getTarget() == null || field.getTarget().isEmpty()) {
                errors.add("Relationship field must specify a target entity");
            }

            if (field.getRelationType() == null || field.getRelationType().isEmpty()) {
                errors.add("Relationship field must specify a relationship type");
            } else if (!VALID_RELATIONSHIP_TYPES.contains(field.getRelationType())) {
                errors.add("Invalid relationship type: " + field.getRelationType());
            }
        }

        // Validate enum fields
        if ("Enum".equals(field.getType())) {
            if (field.getEnumClass() == null || field.getEnumClass().isEmpty()) {
                errors.add("Enum field must specify an enum class name");
            }

            if (field.getValues() == null || field.getValues().isEmpty()) {
                errors.add("Enum field must specify at least one value");
            }
        }

        return new ValidationResult(errors);
    }
}