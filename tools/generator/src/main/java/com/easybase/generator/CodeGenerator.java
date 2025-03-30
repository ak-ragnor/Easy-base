package com.easybase.generator;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.engine.EntityGenerator;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.parser.YamlParser;
import com.easybase.generator.template.TemplateProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main entry point for the code generator.
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

            // Setup the necessary components for code generation
            FileManager fileManager = new FileManager(outputPath);
            TemplateProcessor templateProcessor = new TemplateProcessor();
            EntityGenerator entityGenerator = new EntityGenerator(templateProcessor, fileManager, options);

            // Generate code for each entity
            for (EntityDefinition entity : entities) {
                String entityName = entity.getName();
                System.out.println("Processing entity: " + entityName);

                // Generate all the files for this entity
                entityGenerator.generate(entity);

                // Create a simple info file to track what was processed
                Path entityDir = Paths.get(outputPath, "easybase-" + entityName.toLowerCase());
                Files.createDirectories(entityDir);
                Path infoFilePath = entityDir.resolve("entity-info.txt");
                String infoContent =
                        "Entity Name: " + entityName + "\n" +
                                "Table Name: " + entity.getTable() + "\n" +
                                "Package: " + entity.getPackageName() + "\n" +
                                "Fields: " + entity.getFields().size() + "\n" +
                                "Generation completed at: " + java.time.LocalDateTime.now() + "\n";

                Files.writeString(infoFilePath, infoContent);
            }

        } catch (Exception e) {
            System.err.println("Error parsing YAML file: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error parsing YAML file", e);
        }
    }
}