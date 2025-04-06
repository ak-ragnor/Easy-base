package com.easybase.generator;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.engine.EntityGenerator;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.parser.YamlParser;
import com.easybase.generator.template.TemplateContext;
import com.easybase.generator.template.TemplateProcessor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
            System.err.println("Error during code generation: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error during code generation", e);
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
     * @throws Exception If an error occurs
     */
    private void _updateParentPom(String outputPath, List<EntityDefinition> entities) throws Exception {
        if (entities == null || entities.isEmpty()) {
            System.out.println("No entities to add to parent POM");
            return;
        }

        Path pomPath = Paths.get(outputPath, "pom.xml");
        if (!Files.exists(pomPath)) {
            System.out.println("Parent POM file not found at: " + pomPath);
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(pomPath.toFile());

            // Find or create modules section
            NodeList modulesList = doc.getElementsByTagName("modules");
            Element modulesElement;

            if (modulesList.getLength() == 0) {
                // Create modules section
                modulesElement = doc.createElement("modules");
                Element root = doc.getDocumentElement();
                root.appendChild(modulesElement);
                System.out.println("Created <modules> section in parent POM");
            } else {
                modulesElement = (Element) modulesList.item(0);
            }

            // Get existing modules
            NodeList existingModules = modulesElement.getElementsByTagName("module");
            List<String> existingModuleNames = new ArrayList<>();

            for (int i = 0; i < existingModules.getLength(); i++) {
                Node moduleNode = existingModules.item(i);
                existingModuleNames.add(moduleNode.getTextContent());
            }

            // Add new modules
            boolean modified = false;
            for (EntityDefinition entity : entities) {
                String moduleName = "easybase-" + entity.getName().toLowerCase();

                if (!existingModuleNames.contains(moduleName)) {
                    Element moduleElement = doc.createElement("module");
                    moduleElement.setTextContent(moduleName);
                    modulesElement.appendChild(moduleElement);
                    modified = true;
                    System.out.println("Added module " + moduleName + " to parent POM");
                }
            }

            // Save changes
            if (modified) {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(pomPath.toFile());
                transformer.transform(source, result);
                System.out.println("Updated parent POM at: " + pomPath);
            } else {
                System.out.println("No changes needed for parent POM");
            }
        } catch (Exception e) {
            System.err.println("Error updating parent POM: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}