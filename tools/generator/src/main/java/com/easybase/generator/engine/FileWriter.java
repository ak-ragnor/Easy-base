package com.easybase.generator.engine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Handles writing generated code to files.
 * This class is responsible for creating the necessary directories
 * and writing the generated content to the appropriate files.
 */
public class FileWriter {

    /**
     * Writes content to a file, creating parent directories if needed.
     *
     * @param file The file to write
     * @param content The content to write
     * @throws IOException If there's an error writing the file
     */
    public void writeFile(File file, String content) throws IOException {

        if (fileExistsWithSameContent(file, content)) {
            System.out.println("Skipping unchanged file: " + file.getAbsolutePath());
            return;
        }

        // Ensure parent directories exist
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            boolean dirCreated = parentDir.mkdirs();
            if (!dirCreated && !parentDir.exists()) {
                throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }

        // Write the file
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));

        System.out.println("Generated file: " + file.getAbsolutePath());
    }

    /**
     * Checks if a file with the same content already exists.
     * This prevents unnecessary file updates if the content hasn't changed.
     *
     * @param file The file to check
     * @param content The new content
     * @return True if the file exists with the same content
     */
    public boolean fileExistsWithSameContent(File file, String content) {
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        try {
            String existingContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            return existingContent.equals(content);
        } catch (IOException e) {
            return false;
        }
    }
}