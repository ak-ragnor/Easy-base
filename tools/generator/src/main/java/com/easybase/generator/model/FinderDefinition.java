package com.easybase.generator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a finder method in a repository.
 */
public class FinderDefinition {
    private String name;
    private String returnType;
    private List<Parameter> parameters = new ArrayList<>();
    private String query;

    // Builder pattern
    public static class Builder {
        private FinderDefinition finder = new FinderDefinition();

        public Builder withName(String name) {
            finder.name = name;
            return this;
        }

        public Builder withReturnType(String returnType) {
            finder.returnType = returnType;
            return this;
        }

        public Builder withParameter(Parameter parameter) {
            finder.parameters.add(parameter);
            return this;
        }

        public Builder withParameters(List<Parameter> parameters) {
            finder.parameters.addAll(parameters);
            return this;
        }

        public Builder withQuery(String query) {
            finder.query = query;
            return this;
        }

        public FinderDefinition build() {
            return finder;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Parameter class
    public static class Parameter {
        private String name;
        private String type;

        public Parameter() {
        }

        public Parameter(String name, String type) {
            this.name = name;
            this.type = type;
        }

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

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    // Utility methods

    /**
     * Gets the method signature for this finder method.
     */
    public String getMethodSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType).append(" ").append(name).append("(");

        for (int i = 0; i < parameters.size(); i++) {
            Parameter param = parameters.get(i);
            sb.append(param.getType()).append(" ").append(param.getName());

            if (i < parameters.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Gets the parameter list for method calls.
     */
    public String getParameterCallList() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parameters.size(); i++) {
            sb.append(parameters.get(i).getName());

            if (i < parameters.size() - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    /**
     * Gets all imports required for this finder method.
     */
    public List<String> getRequiredImports() {
        List<String> imports = new ArrayList<>();

        // Handle return type imports
        if (returnType.startsWith("List<")) {
            imports.add("java.util.List");
        } else if (returnType.startsWith("Optional<")) {
            imports.add("java.util.Optional");
        } else if (returnType.startsWith("Page<")) {
            imports.add("org.springframework.data.domain.Page");
        }

        // Handle parameter type imports
        for (Parameter param : parameters) {
            if (param.getType().equals("Pageable")) {
                imports.add("org.springframework.data.domain.Pageable");
            } else if (param.getType().equals("UUID")) {
                imports.add("java.util.UUID");
            }
        }

        return imports;
    }
}