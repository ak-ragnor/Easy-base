package com.easybase.core.dataengine.util;

public final class NamingUtils {

    private NamingUtils() {}

    public static String sanitizeCollectionName(String collectionName) {
        if (collectionName == null || collectionName.isBlank()) {
            throw new IllegalArgumentException("Collection name cannot be blank");
        }
        if (collectionName.length() > 63) {
            throw new IllegalArgumentException("Collection name cannot exceed 63 characters");
        }
        if (!collectionName.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            throw new IllegalArgumentException("Collection name must start with a letter and contain only letters, numbers, and underscores");
        }
        return collectionName;
    }

    public static String sanitizeAttributeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Attribute name cannot be blank");
        }
        if (name.length() > 63) {
            throw new IllegalArgumentException("Attribute name cannot exceed 63 characters");
        }
        if (!name.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            throw new IllegalArgumentException("Attribute name must start with a letter and contain only letters, numbers, and underscores");
        }
        return name;
    }
}
