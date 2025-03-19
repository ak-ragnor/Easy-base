package com.easybase.core.search.model.operator;

/**
 * Enumeration of function types supported in filter expressions.
 */
public enum FunctionType {
    CONTAINS("contains");  // String contains function

    private final String name;

    FunctionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static FunctionType fromName(String name) {
        for (FunctionType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown function: " + name);
    }
}