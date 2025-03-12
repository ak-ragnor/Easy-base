package com.easybase.generator.validators;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a validation operation.
 * This class contains a list of validation errors, if any.
 */
public class ValidationResult {
    private List<String> errors;

    /**
     * Constructs a new ValidationResult with a list of errors.
     *
     * @param errors The list of validation errors
     */
    public ValidationResult(List<String> errors) {
        this.errors = new ArrayList<>(errors);
    }

    /**
     * Gets the list of validation errors.
     *
     * @return The list of validation errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Checks if the validation was successful (no errors).
     *
     * @return True if there are no errors
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Gets a string representation of all validation errors.
     *
     * @return A string containing all validation errors
     */
    public String getErrorMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Validation failed with ").append(errors.size()).append(" errors:\n");

        for (int i = 0; i < errors.size(); i++) {
            sb.append(i + 1).append(". ").append(errors.get(i)).append("\n");
        }

        return sb.toString();
    }
}