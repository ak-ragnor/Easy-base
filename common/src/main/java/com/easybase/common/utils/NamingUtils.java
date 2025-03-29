package com.easybase.common.utils;

/**
 * Utility class for handling naming conventions.
 */
public class NamingUtils {

    /**
     * Converts a string to camel case.
     *
     * @param input The input string
     * @return The camel case string
     */
    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : input.toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    }

    /**
     * Converts a string to pascal case.
     *
     * @param input The input string
     * @return The pascal case string
     */
    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String camelCase = toCamelCase(input);
        return Character.toUpperCase(camelCase.charAt(0)) + camelCase.substring(1);
    }

    /**
     * Converts a string to snake case.
     *
     * @param input The input string
     * @return The snake case string
     */
    public static String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(input.charAt(0)));

        for (int i = 1; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_');
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Converts a string to kebab case.
     *
     * @param input The input string
     * @return The kebab case string
     */
    public static String toKebabCase(String input) {
        return toSnakeCase(input).replace('_', '-');
    }

    /**
     * Converts a string to a plural form.
     *
     * Note: This is a simple implementation that works for most cases.
     * In a real implementation, you might want to use a more sophisticated
     * library like Inflector to handle all edge cases.
     *
     * @param input The input string
     * @return The plural form of the string
     */
    public static String toPlural(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        if (input.endsWith("y")) {
            return input.substring(0, input.length() - 1) + "ies";
        } else if (input.endsWith("s") || input.endsWith("x") || input.endsWith("z") ||
                input.endsWith("ch") || input.endsWith("sh")) {
            return input + "es";
        } else {
            return input + "s";
        }
    }

    /**
     * Converts a string to a singular form.
     *
     * Note: This is a simple implementation that works for most cases.
     * In a real implementation, you might want to use a more sophisticated
     * library like Inflector to handle all edge cases.
     *
     * @param input The input string
     * @return The singular form of the string
     */
    public static String toSingular(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        if (input.endsWith("ies")) {
            return input.substring(0, input.length() - 3) + "y";
        } else if (input.endsWith("es") && (input.endsWith("sses") || input.endsWith("xes") ||
                input.endsWith("zes") || input.endsWith("ches") ||
                input.endsWith("shes"))) {
            return input.substring(0, input.length() - 2);
        } else if (input.endsWith("s") && input.length() > 1) {
            return input.substring(0, input.length() - 1);
        } else {
            return input;
        }
    }
}