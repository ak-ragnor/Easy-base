package com.easybase.generator.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages file operations for the code generator.
 */
public class FileManager {

    private final String rootDirectory;
    private final FileWriter fileWriter;
    private final CustomCodePreserver customCodePreserver;
    private final ProjectScanner projectScanner;

    // Cache of file contents to minimize disk I/O
    private final Map<Path, String> fileCache = new HashMap<>();

    /**
     * Constructs a new FileManager.
     *
     * @param rootDirectory The root directory for file operations
     */
    public FileManager(String rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.fileWriter = new FileWriter();
        this.customCodePreserver = new CustomCodePreserver();
        this.projectScanner = new ProjectScanner();
    }

    /**
     * Writes a file, preserving custom code if the file already exists.
     *
     * @param relativePath The relative path of the file
     * @param content The content to write
     * @param isBase Whether this is a base file (which should always overwrite existing content)
     * @throws IOException If an I/O error occurs
     */
    public void writeFile(String relativePath, String content, boolean isBase) throws IOException {
        Path filePath = Paths.get(rootDirectory, relativePath);

        // Create parent directories if they don't exist
        Files.createDirectories(filePath.getParent());

        if (Files.exists(filePath) && !isBase) {
            // This is a custom file, so we need to preserve custom code
            String existingContent = _getFileContent(filePath);
            String mergedContent = customCodePreserver.merge(existingContent, content);
            fileWriter.write(filePath, mergedContent);
        } else {
            // This is a new file or a base file, so we can write it directly
            fileWriter.write(filePath, content);
        }

        // Update the cache
        fileCache.put(filePath, content);
    }

    /**
     * Checks if a file exists.
     *
     * @param relativePath The relative path of the file
     * @return True if the file exists, false otherwise
     */
    public boolean fileExists(String relativePath) {
        Path filePath = Paths.get(rootDirectory, relativePath);
        return Files.exists(filePath);
    }

    /**
     * Gets the content of a file.
     *
     * @param relativePath The relative path of the file
     * @return The content of the file
     * @throws IOException If an I/O error occurs
     */
    public String getFileContent(String relativePath) throws IOException {
        Path filePath = Paths.get(rootDirectory, relativePath);
        return _getFileContent(filePath);
    }

    /**
     * Gets the content of a file.
     *
     * @param filePath The path of the file
     * @return The content of the file
     * @throws IOException If an I/O error occurs
     */
    private String _getFileContent(Path filePath) throws IOException {
        // Check the cache first
        if (fileCache.containsKey(filePath)) {
            return fileCache.get(filePath);
        }

        // Read from disk
        String content = new String(Files.readAllBytes(filePath));

        // Update the cache
        fileCache.put(filePath, content);

        return content;
    }

    /**
     * Scans for existing Java files in the project.
     *
     * @return A map of file paths to file contents
     * @throws IOException If an I/O error occurs
     */
    public Map<String, String> scanForJavaFiles() throws IOException {
        return projectScanner.scanForJavaFiles(rootDirectory);
    }

    /**
     * Gets the root directory.
     *
     * @return The root directory
     */
    public String getRootDirectory() {
        return rootDirectory;
    }
}