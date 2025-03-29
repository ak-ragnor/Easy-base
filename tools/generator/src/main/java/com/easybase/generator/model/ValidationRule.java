package com.easybase.generator.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a validation rule for a field.
 */
public class ValidationRule {
    private String type;
    private String message;
    private Map<String, Object> parameters = new HashMap<>();

    // Builder pattern
    public static class Builder {
        private ValidationRule rule = new ValidationRule();

        public Builder withType(String type) {
            rule.type = type;
            return this;
        }

        public Builder withMessage(String message) {
            rule.message = message;
            return this;
        }

        public Builder withParameter(String key, Object value) {
            rule.parameters.put(key, value);
            return this;
        }

        public Builder withParameters(Map<String, Object> parameters) {
            rule.parameters.putAll(parameters);
            return this;
        }

        public ValidationRule build() {
            return rule;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the validation annotation for this rule.
     */
    public String getAnnotation() {
        StringBuilder sb = new StringBuilder("@");
        sb.append(type);

        // Add parameters
        if (!parameters.isEmpty() || message != null) {
            sb.append("(");

            // Add message if present
            if (message != null) {
                sb.append("message = \"").append(message).append("\"");
                if (!parameters.isEmpty()) {
                    sb.append(", ");
                }
            }

            // Add other parameters
            int count = 0;
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                if (count > 0) {
                    sb.append(", ");
                }

                sb.append(entry.getKey()).append(" = ");

                // Format the value based on its type
                Object value = entry.getValue();
                if (value instanceof String) {
                    sb.append("\"").append(value).append("\"");
                } else if (value instanceof Boolean) {
                    sb.append(value);
                } else if (value instanceof Number) {
                    sb.append(value);
                } else {
                    sb.append(value);
                }

                count++;
            }

            sb.append(")");
        }

        return sb.toString();
    }

    /**
     * Gets the import statement for this validation type.
     */
    public String getImport() {
        // Most validation annotations are in jakarta.validation.constraints
        return "jakarta.validation.constraints." + type;
    }
}