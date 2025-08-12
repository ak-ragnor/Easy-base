package com.easybase.core.dataengine.enums;

import lombok.Getter;

/**
 * Defines the allowed data types for user-defined attributes
 */
@Getter
public enum AttributeType {

    TEXT("text") {
        @Override
        public String getPostgresType() {
            return "TEXT";
        }
    },
    NUMBER("number") {
        @Override
        public String getPostgresType() {
            return "NUMERIC";
        }
    },
    DATE("date") {
        @Override
        public String getPostgresType() {
            return "DATE";
        }
    },
    DECIMAL("decimal") {
        @Override
        public String getPostgresType() {
            return "NUMERIC(19,4)";
        }
    };

    private final String code;

    AttributeType(String code) {
        this.code = code;
    }

    public abstract String getPostgresType();

    public static AttributeType fromCode(String code) {
        for (AttributeType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown AttributeType: " + code);
    }
}
