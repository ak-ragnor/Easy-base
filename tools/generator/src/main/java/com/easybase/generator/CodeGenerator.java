package com.easybase.generator;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.engine.EntityGenerator;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.parser.YamlParser;
import com.easybase.generator.template.TemplateProcessor;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main entry point for the code generator.
 */
public class CodeGenerator {

    private final YamlParser yamlParser;
    private final EntityGenerator entityGenerator;

    /**
     * Constructs a new CodeGenerator with the given options.
     *
     * @param options The generation options
     */
    public CodeGenerator(GenerationOptions options) {
        this.yamlParser = new YamlParser();

        TemplateProcessor templateProcessor = new TemplateProcessor();
        FileManager fileManager = new FileManager(options.getOutputDirectory());

        this.entityGenerator = new EntityGenerator(templateProcessor, fileManager, options);
    }

    /**
     * Generates code from a YAML file.
     *
     * @param yamlFile The YAML file
     * @throws IOException If an I/O error occurs
     */
    public void generate(File yamlFile) throws IOException {
        // Parse the YAML file
        List<EntityDefinition> entities = yamlParser.parse(yamlFile);

        // Generate code for all entities
        entityGenerator.generateAll(entities);
    }

    /**
     * Main method for command-line usage.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar code-generator.jar <yaml-file> [output-directory]");
            System.exit(1);
        }

        String yamlFilePath = args[0];
        File yamlFile = new File(yamlFilePath);

        if (!yamlFile.exists()) {
            System.err.println("YAML file not found: " + yamlFilePath);
            System.exit(1);
        }

        // Create options
        GenerationOptions.Builder optionsBuilder = GenerationOptions.builder();

        // Set output directory if provided
        if (args.length > 1) {
            optionsBuilder.outputDirectory(args[1]);
        }

        GenerationOptions options = optionsBuilder.build();

        try {
            // Create and run the generator
            CodeGenerator generator = new CodeGenerator(options);
            generator.generate(yamlFile);

            System.out.println("Code generation completed successfully.");
        } catch (IOException e) {
            System.err.println("Error generating code: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}