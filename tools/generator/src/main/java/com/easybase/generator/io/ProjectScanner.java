package com.easybase.generator.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * Scans for existing files in the project.
 */
public class ProjectScanner {

    /**
     * Scans for existing Java files in the project.
     *
     * @param rootDirectory The root directory
     * @return A map of file paths to file contents
     * @throws IOException If an I/O error occurs
     */
    public Map<String, String> scanForJavaFiles(String rootDirectory) throws IOException {
        Map<String, String> javaFiles = new HashMap<>();
        Path rootPath = Paths.get(rootDirectory);

        Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java")) {
                    String relativePath = rootPath.relativize(file).toString();
                    String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                    javaFiles.put(relativePath, content);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return javaFiles;
    }

    /**
     * Scans for existing files in the project matching a specific pattern.
     *
     * @param rootDirectory The root directory
     * @param pattern The pattern to match
     * @return A map of file paths to file contents
     * @throws IOException If an I/O error occurs
     */
    public Map<String, String> scanForFiles(String rootDirectory, String pattern) throws IOException {
        Map<String, String> matchingFiles = new HashMap<>();
        Path rootPath = Paths.get(rootDirectory);

        Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().matches(pattern)) {
                    String relativePath = rootPath.relativize(file).toString();
                    String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                    matchingFiles.put(relativePath, content);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return matchingFiles;
    }
}