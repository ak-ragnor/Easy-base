package com.easybase.generator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for analyzing Java code.
 */
public class CodeAnalyzer {

    // Pattern for extracting the package from Java code
    private static final Pattern PACKAGE_PATTERN = Pattern.compile(
            "package\\s+([\\w.]+);",
            Pattern.MULTILINE
    );

    // Pattern for extracting class or interface name from Java code
    private static final Pattern CLASS_PATTERN = Pattern.compile(
            "(public|protected|private)?\\s*(class|interface|enum)\\s+(\\w+)",
            Pattern.MULTILINE
    );

    // Pattern for extracting import statements from Java code
    private static final Pattern IMPORT_PATTERN = Pattern.compile(
            "import\\s+([\\w.]+);",
            Pattern.MULTILINE
    );

    // Pattern for extracting method signatures from Java code
    private static final Pattern METHOD_PATTERN = Pattern.compile(
            "(public|protected|private)?\\s*(static)?\\s*([\\w<>\\[\\],\\s]+)\\s+(\\w+)\\s*\\(([^)]*)\\)",
            Pattern.MULTILINE
    );

    /**
     * Extracts the package from Java code.
     *
     * @param code The Java code
     * @return The package, or null if not found
     */
    public static String extractPackage(String code) {
        Matcher matcher = PACKAGE_PATTERN.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Extracts the class or interface name from Java code.
     *
     * @param code The Java code
     * @return The class or interface name, or null if not found
     */
    public static String extractClassName(String code) {
        Matcher matcher = CLASS_PATTERN.matcher(code);
        if (matcher.find()) {
            return matcher.group(3);
        }
        return null;
    }

    /**
     * Extracts import statements from Java code.
     *
     * @param code The Java code
     * @return The list of imports
     */
    public static List<String> extractImports(String code) {
        List<String> imports = new ArrayList<>();
        Matcher matcher = IMPORT_PATTERN.matcher(code);
        while (matcher.find()) {
            imports.add(matcher.group(1));
        }
        return imports;
    }

    /**
     * Extracts method signatures from Java code.
     *
     * @param code The Java code
     * @return The list of method signatures
     */
    public static List<String> extractMethodSignatures(String code) {
        List<String> methods = new ArrayList<>();
        Matcher matcher = METHOD_PATTERN.matcher(code);
        while (matcher.find()) {
            String visibility = matcher.group(1);
            String staticModifier = matcher.group(2);
            String returnType = matcher.group(3);
            String methodName = matcher.group(4);
            String parameters = matcher.group(5);

            StringBuilder signature = new StringBuilder();
            if (visibility != null) {
                signature.append(visibility).append(" ");
            }
            if (staticModifier != null) {
                signature.append(staticModifier).append(" ");
            }
            signature.append(returnType.trim()).append(" ");
            signature.append(methodName).append("(").append(parameters).append(")");

            methods.add(signature.toString());
        }
        return methods;
    }

    /**
     * Checks if a class contains a specific method signature.
     *
     * @param code The Java code
     * @param methodSignature The method signature to look for
     * @return True if the method signature is found, false otherwise
     */
    public static boolean containsMethod(String code, String methodSignature) {
        List<String> methods = extractMethodSignatures(code);
        return methods.stream()
                .anyMatch(method -> method.matches(".*\\s+" + Pattern.quote(methodSignature) + "\\s*\\(.*"));
    }
}