package com.easybase.generator.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes files to disk.
 */
public class FileWriter {

    /**
     * Writes content to a file.
     *
     * @param path The path of the file
     * @param content The content to write
     * @throws IOException If an I/O error occurs
     */
    public void write(Path path, String content) throws IOException {
        // Ensure parent directories exist
        Files.createDirectories(path.getParent());

        // Write the file
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Checks if a file with the same content already exists.
     *
     * @param path The path of the file
     * @param content The content to check
     * @return True if the file exists with the same content, false otherwise
     * @throws IOException If an I/O error occurs
     */
    public boolean fileExistsWithSameContent(Path path, String content) throws IOException {
        if (!Files.exists(path)) {
            return false;
        }

        String existingContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return existingContent.equals(content);
    }
}