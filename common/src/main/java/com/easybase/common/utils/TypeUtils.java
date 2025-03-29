package com.easybase.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling type conversions.
 */
public class TypeUtils {

    private static final Map<String, String> JAVA_TO_SQL_TYPE = new HashMap<>();
    private static final Map<String, String> JAVA_TO_ES_TYPE = new HashMap<>();

    static {
        // Initialize Java to SQL type mapping
        JAVA_TO_SQL_TYPE.put("String", "VARCHAR(255)");
        JAVA_TO_SQL_TYPE.put("Integer", "INTEGER");
        JAVA_TO_SQL_TYPE.put("Long", "BIGINT");
        JAVA_TO_SQL_TYPE.put("Double", "DOUBLE PRECISION");
        JAVA_TO_SQL_TYPE.put("Float", "FLOAT");
        JAVA_TO_SQL_TYPE.put("Boolean", "BOOLEAN");
        JAVA_TO_SQL_TYPE.put("Date", "DATE");
        JAVA_TO_SQL_TYPE.put("DateTime", "TIMESTAMP");
        JAVA_TO_SQL_TYPE.put("Instant", "TIMESTAMP");
        JAVA_TO_SQL_TYPE.put("LocalDate", "DATE");
        JAVA_TO_SQL_TYPE.put("LocalDateTime", "TIMESTAMP");
        JAVA_TO_SQL_TYPE.put("BigDecimal", "DECIMAL(19, 2)");
        JAVA_TO_SQL_TYPE.put("UUID", "VARCHAR(36)");

        // Initialize Java to Elasticsearch type mapping
        JAVA_TO_ES_TYPE.put("String", "text");
        JAVA_TO_ES_TYPE.put("Integer", "integer");
        JAVA_TO_ES_TYPE.put("Long", "long");
        JAVA_TO_ES_TYPE.put("Double", "double");
        JAVA_TO_ES_TYPE.put("Float", "float");
        JAVA_TO_ES_TYPE.put("Boolean", "boolean");
        JAVA_TO_ES_TYPE.put("Date", "date");
        JAVA_TO_ES_TYPE.put("DateTime", "date");
        JAVA_TO_ES_TYPE.put("Instant", "date");
        JAVA_TO_ES_TYPE.put("LocalDate", "date");
        JAVA_TO_ES_TYPE.put("LocalDateTime", "date");
        JAVA_TO_ES_TYPE.put("BigDecimal", "scaled_float");
        JAVA_TO_ES_TYPE.put("UUID", "keyword");
    }

    /**
     * Converts a Java type to a SQL type.
     *
     * @param javaType The Java type
     * @return The SQL type
     */
    public static String javaToSqlType(String javaType) {
        return JAVA_TO_SQL_TYPE.getOrDefault(javaType, "VARCHAR(255)");
    }

    /**
     * Converts a Java type to an Elasticsearch type.
     *
     * @param javaType The Java type
     * @return The Elasticsearch type
     */
    public static String javaToEsType(String javaType) {
        return JAVA_TO_ES_TYPE.getOrDefault(javaType, "keyword");
    }

    /**
     * Gets the default value for a Java type.
     *
     * @param javaType The Java type
     * @return The default value
     */
    public static String getDefaultValue(String javaType) {
        switch (javaType) {
            case "String":
                return "null";
            case "Integer":
                return "0";
            case "Long":
                return "0L";
            case "Double":
                return "0.0";
            case "Float":
                return "0.0f";
            case "Boolean":
                return "false";
            case "Date":
            case "DateTime":
            case "Instant":
            case "LocalDate":
            case "LocalDateTime":
            case "BigDecimal":
            case "UUID":
                return "null";
            default:
                return "null";
        }
    }

    /**
     * Checks if a Java type is a simple type.
     *
     * @param javaType The Java type
     * @return True if the type is a simple type, false otherwise
     */
    public static boolean isSimpleType(String javaType) {
        switch (javaType) {
            case "String":
            case "Integer":
            case "Long":
            case "Double":
            case "Float":
            case "Boolean":
            case "Date":
            case "DateTime":
            case "Instant":
            case "LocalDate":
            case "LocalDateTime":
            case "BigDecimal":
            case "UUID":
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if a Java type is a numeric type.
     *
     * @param javaType The Java type
     * @return True if the type is a numeric type, false otherwise
     */
    public static boolean isNumericType(String javaType) {
        switch (javaType) {
            case "Integer":
            case "Long":
            case "Double":
            case "Float":
            case "BigDecimal":
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if a Java type is a date type.
     *
     * @param javaType The Java type
     * @return True if the type is a date type, false otherwise
     */
    public static boolean isDateType(String javaType) {
        switch (javaType) {
            case "Date":
            case "DateTime":
            case "Instant":
            case "LocalDate":
            case "LocalDateTime":
                return true;
            default:
                return false;
        }
    }
}
