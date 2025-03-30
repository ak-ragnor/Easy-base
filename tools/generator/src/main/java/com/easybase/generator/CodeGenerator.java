package com.easybase.generator;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.parser.YamlParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main entry point for the code generator.
 * This is a temporary debugging implementation that creates a simple file.
 */
public class CodeGenerator {

    private final GenerationOptions options;

    /**
     * Constructs a new CodeGenerator with the given options.
     *
     * @param options The generation options
     */
    public CodeGenerator(GenerationOptions options) {
        this.options = options;
        // Print debug output
        System.out.println("CodeGenerator initialized with options: " + options);
    }

    /**
     * Generates code from a YAML file.
     *
     * @param yamlFile The YAML file
     * @throws IOException If an I/O error occurs
     */
    public void generate(File yamlFile) throws IOException {
        System.out.println("Starting code generation from YAML file: " + yamlFile.getAbsolutePath());

        // Create a simple test file to verify write access
        String outputPath = options.getOutputDirectory();
        System.out.println("Output directory: " + outputPath);

        // Create test file
        Path testFilePath = Paths.get(outputPath, "generation-test.txt");
        try {
            Files.writeString(testFilePath, "Generation test file. Created from CodeGenerator.generate method.");
            System.out.println("Created test file at: " + testFilePath);
        } catch (IOException e) {
            System.err.println("Failed to create test file: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // Parse the YAML file
        try {
            System.out.println("Parsing YAML file...");
            YamlParser parser = new YamlParser();
            List<EntityDefinition> entities = parser.parse(yamlFile);
            System.out.println("Parsed " + entities.size() + " entities from YAML file");

            // Create output directory for each entity
            for (EntityDefinition entity : entities) {
                String entityName = entity.getName();
                System.out.println("Processing entity: " + entityName);

                // Create entity directory
                Path entityDir = Paths.get(outputPath, "easybase-" + entityName.toLowerCase());
                Files.createDirectories(entityDir);
                System.out.println("Created entity directory: " + entityDir);

                // Create a simple info file
                Path infoFilePath = entityDir.resolve("entity-info.txt");
                String infoContent =
                        "Entity Name: " + entityName + "\n" +
                                "Table Name: " + entity.getTable() + "\n" +
                                "Package: " + entity.getPackageName() + "\n" +
                                "Fields: " + entity.getFields().size() + "\n";

                Files.writeString(infoFilePath, infoContent);
                System.out.println("Created entity info file at: " + infoFilePath);
            }

        } catch (Exception e) {
            System.err.println("Error parsing YAML file: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error parsing YAML file", e);
        }
    }
}