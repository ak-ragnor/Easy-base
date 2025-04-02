// File: tools/generator/src/main/java/com/easybase/generator/CodeGenerator.java
package com.easybase.generator;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.engine.EntityGenerator;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.parser.YamlParser;
import com.easybase.generator.template.TemplateContext;
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

        String outputPath = options.getOutputDirectory();
        System.out.println("Output directory: " + outputPath);

        // Parse the YAML file
        try {
            System.out.println("Parsing YAML file...");
            YamlParser parser = new YamlParser();
            List<EntityDefinition> entities = parser.parse(yamlFile);
            System.out.println("Parsed " + entities.size() + " entities from YAML file");

            // Create a shared template processor
            TemplateProcessor templateProcessor = new TemplateProcessor();

            for (EntityDefinition entity : entities) {
                String entityName = entity.getName();
                System.out.println("Processing entity: " + entityName);

                // Create module directory path
                String moduleName = "easybase-" + entityName.toLowerCase();
                Path moduleDir = Paths.get(outputPath, moduleName);
                Files.createDirectories(moduleDir);

                // Create src/main/java directory
                Path srcMainJavaDir = moduleDir.resolve(Paths.get("src", "main", "java"));
                Files.createDirectories(srcMainJavaDir);

                // Create src/main/resources directory
                Path srcMainResourcesDir = moduleDir.resolve(Paths.get("src", "main", "resources"));
                Files.createDirectories(srcMainResourcesDir);

                // Create src/test/java directory
                Path srcTestJavaDir = moduleDir.resolve(Paths.get("src", "test", "java"));
                Files.createDirectories(srcTestJavaDir);

                // Set up for code generation with correct path
                String moduleOutputPath = moduleDir.toString();

                // Create module-specific options
                GenerationOptions moduleOptions = GenerationOptions.builder()
                        .outputDirectory(moduleOutputPath)
                        .generateTests(options.isGenerateTests())
                        .overwriteCustomCode(options.isOverwriteCustomCode())
                        .verbose(options.isVerbose())
                        .build();

                // Setup FileManager for code generation
                FileManager fileManager = new FileManager(moduleOutputPath);
                EntityGenerator entityGenerator = new EntityGenerator(templateProcessor, fileManager, moduleOptions);

                // Generate code
                entityGenerator.generate(entity);

                // Create module POM file
                _generateModulePom(moduleDir, entity, templateProcessor);

                // Create info file
                Path infoFilePath = moduleDir.resolve("entity-info.txt");
                String infoContent =
                        "Entity Name: " + entityName + "\n" +
                                "Table Name: " + entity.getTable() + "\n" +
                                "Package: " + entity.getPackageName() + "\n" +
                                "Fields: " + entity.getFields().size() + "\n" +
                                "Generation completed at: " + java.time.LocalDateTime.now() + "\n";
                Files.writeString(infoFilePath, infoContent);
            }

            // Update parent POM
            _updateParentPom(outputPath, entities);

        } catch (Exception e) {
            System.err.println("Error parsing YAML file: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error parsing YAML file", e);
        }
    }

    /**
     * Generates a POM file for the module using a template.
     *
     * @param moduleDir The module directory
     * @param entity The entity definition
     * @param templateProcessor The template processor to use
     * @throws IOException If an I/O error occurs
     */
    private void _generateModulePom(Path moduleDir, EntityDefinition entity, TemplateProcessor templateProcessor) throws IOException {
        String entityName = entity.getName().toLowerCase();
        String moduleName = "easybase-" + entityName;

        // Create template context
        TemplateContext context = TemplateContext.create();
        context.set("entity", entity);
        context.set("moduleName", moduleName);

        // Process template
        String pomContent = templateProcessor.process("module/pom.ftl", context);

        // Write POM file
        Path pomPath = moduleDir.resolve("pom.xml");
        Files.writeString(pomPath, pomContent);

        System.out.println("Generated POM file for module: " + moduleName);
    }

    /**
     * Updates the parent POM file to include the new module.
     *
     * @param outputPath The output directory
     * @param entities The list of entity definitions
     * @throws IOException If an I/O error occurs
     */
    private void _updateParentPom(String outputPath, List<EntityDefinition> entities) throws IOException {
        if (entities == null) {
            System.out.println("Entities list is null");
            return;
        }
        Path pomPath = Paths.get(outputPath, "pom.xml");

        if (!Files.exists(pomPath)) {
            System.out.println("Parent POM file not found at: " + pomPath);
            return;
        }

        String pomContent = Files.readString(pomPath);

        // Check if modules section exists
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(pomPath.toFile());
        NodeList moduleNodes = doc.getElementsByTagName("modules");
        if (moduleNodes.getLength() == 0) {
            System.out.println("Could not find <modules> section in parent POM");
            return;
        }

        if (modulesStart == -1 || modulesEnd == -1) {
            System.out.println("Could not find <modules> section in parent POM");
            return;
        }

        // Get existing modules content
        String modulesContent = pomContent.substring(modulesStart + 9, modulesEnd).trim();

        // Create new modules content
        StringBuilder newModulesContent = new StringBuilder(modulesContent);

        for (EntityDefinition entity : entities) {
            String moduleName = "easybase-" + entity.getName().toLowerCase();

            // Check if module already exists
            if (!modulesContent.contains("<module>" + moduleName + "</module>")) {
                if (!modulesContent.isEmpty()) {
                    newModulesContent.append("\n        ");
                }
                newModulesContent.append("<module>").append(moduleName).append("</module>");
            }
        }

        // Replace modules content
        String newPomContent = pomContent.substring(0, modulesStart + 9) +
                "\n        " + newModulesContent + "\n    " +
                pomContent.substring(modulesEnd);

        // Write updated POM
        Files.writeString(pomPath, newPomContent);

        System.out.println("Updated parent POM at: " + pomPath);
    }
}