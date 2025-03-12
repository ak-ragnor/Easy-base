package com.easybase.generator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a finder method defined for an entity.
 * Finder methods are query methods that are generated in the repository.
 */
public class FinderDefinition {
    private String name;
    private List<ParameterDefinition> parameters = new ArrayList<>();
    private String returnType;
    private String query;

    // Constructors
    public FinderDefinition() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDefinition> parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Generates the method signature for this finder method.
     *
     * @return The method signature as a string
     */
    public String getMethodSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType).append(" ").append(name).append("(");

        for (int i = 0; i < parameters.size(); i++) {
            ParameterDefinition param = parameters.get(i);
            sb.append(param.getType()).append(" ").append(param.getName());
            if (i < parameters.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Gets the list of imports required for this finder method.
     *
     * @param entityName The name of the entity
     * @return List of imports
     */
    public List<String> getRequiredImports(String entityName) {
        List<String> imports = new ArrayList<>();

        // Add import for return type
        if (returnType.startsWith("List<")) {
            imports.add("java.util.List");
        } else if (returnType.startsWith("Optional<")) {
            imports.add("java.util.Optional");
        } else if (returnType.startsWith("Page<")) {
            imports.add("org.springframework.data.domain.Page");
            imports.add("org.springframework.data.domain.Pageable");
        }

        // Add imports for parameters
        for (ParameterDefinition param : parameters) {
            if ("UUID".equals(param.getType())) {
                imports.add("java.util.UUID");
            } else if ("Pageable".equals(param.getType())) {
                imports.add("org.springframework.data.domain.Pageable");
            }
        }

        return imports;
    }

    /**
     * Nested class to represent a parameter in a finder method.
     */
    public static class ParameterDefinition {
        private String name;
        private String type;

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
    }
}