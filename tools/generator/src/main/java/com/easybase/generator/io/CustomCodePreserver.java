package com.easybase.generator.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Preserves custom code during file generation.
 */
public class CustomCodePreserver {

    // Pattern for custom code sections
    private static final Pattern CUSTOM_CODE_PATTERN = Pattern.compile(
            "// CUSTOM CODE START: (\\w+)\\s*([\\s\\S]*?)\\s*// CUSTOM CODE END: (\\1)",
            Pattern.MULTILINE
    );

    /**
     * Merges the existing content and the new content, preserving custom code sections.
     *
     * @param existingContent The existing content
     * @param newContent The new content
     * @return The merged content
     */
    public String merge(String existingContent, String newContent) {
        if (existingContent == null || existingContent.isEmpty()) {
            return newContent;
        }

        if (newContent == null || newContent.isEmpty()) {
            return existingContent;
        }

        // Extract custom code sections from the existing content
        Matcher matcher = CUSTOM_CODE_PATTERN.matcher(existingContent);
        StringBuffer result = new StringBuffer(newContent);

        while (matcher.find()) {
            String sectionName = matcher.group(1);
            String customCode = matcher.group(2);

            // Replace the section in the new content
            String sectionPattern = "// CUSTOM CODE START: " + sectionName + "\\s*([\\s\\S]*?)\\s*// CUSTOM CODE END: " + sectionName;
            Pattern pattern = Pattern.compile(sectionPattern, Pattern.MULTILINE);
            Matcher newMatcher = pattern.matcher(result);

            if (newMatcher.find()) {
                result.replace(newMatcher.start(1), newMatcher.end(1), customCode);
            }
        }

        return result.toString();
    }

    /**
     * Checks if a file contains custom code sections.
     *
     * @param content The file content
     * @return True if the file contains custom code sections, false otherwise
     */
    public boolean hasCustomCode(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }

        Matcher matcher = CUSTOM_CODE_PATTERN.matcher(content);
        return matcher.find();
    }

    /**
     * Creates a custom code section marker.
     *
     * @param sectionName The section name
     * @param initialCode The initial code for the section
     * @return The custom code section marker
     */
    public String createCustomCodeSection(String sectionName, String initialCode) {
        return "// CUSTOM CODE START: " + sectionName + "\n" +
                (initialCode != null ? initialCode : "") + "\n" +
                "// CUSTOM CODE END: " + sectionName;
    }
}