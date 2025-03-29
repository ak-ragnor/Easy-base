package com.easybase.generator.model;

/**
 * Represents an entity lifecycle listener.
 */
public class ListenerDefinition {
    private String type;
    private String method;

    // Builder pattern
    public static class Builder {
        private ListenerDefinition listener = new ListenerDefinition();

        public Builder withType(String type) {
            listener.type = type;
            return this;
        }

        public Builder withMethod(String method) {
            listener.method = method;
            return this;
        }

        public ListenerDefinition build() {
            return listener;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets the annotation for this listener.
     */
    public String getAnnotation() {
        return "@" + type;
    }

    /**
     * Gets the import for this listener.
     */
    public String getImport() {
        return "jakarta.persistence." + type;
    }
}