package com.easybase.generator.engine;

import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.model.FieldDefinition;
import com.easybase.generator.parser.YamlParser;
import com.easybase.generator.validators.EntityValidator;
import com.easybase.generator.validators.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main code generation orchestrator.
 * This class coordinates the entire generation process, from YAML parsing
 * to file writing, delegating to specialized components as needed.
 */
public class Generator {
    private YamlParser parser;
    private EntityValidator validator;
    private TemplateProcessor templateProcessor;
    private FileWriter fileWriter;

    // Base output directory
    private String outputDirectory = "apps";

    /**
     * Constructs a new Generator with all required components.
     */
    public Generator() {
        this.parser = new YamlParser();
        this.validator = new EntityValidator();
        this.templateProcessor = new TemplateProcessor();
        this.fileWriter = new FileWriter();
    }

    /**
     * Sets the output directory for generated code.
     *
     * @param outputDirectory The output directory path
     */
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Generates code from a YAML file.
     *
     * @param yamlFile The YAML file containing the entity definition
     * @return True if generation was successful
     * @throws IOException If there's an error reading or writing files
     */
    public boolean generate(File yamlFile) throws IOException {
        System.out.println("Parsing YAML file: " + yamlFile.getAbsolutePath());
        EntityDefinition entity = parser.parse(yamlFile);

        // Validate the entity definition
        ValidationResult validationResult = validator.validate(entity);
        if (!validationResult.isValid()) {
            System.err.println(validationResult.getErrorMessage());
            return false;
        }

        // Create the module directory
        String moduleName = entity.getModule();
        File moduleDir = new File(outputDirectory, moduleName);
        if (!moduleDir.exists()) {
            boolean created = moduleDir.mkdirs();
            if (!created && !moduleDir.exists()) {
                throw new IOException("Failed to create module directory: " + moduleDir.getAbsolutePath());
            }
        }

        // Generate the module POM
        generateModulePom(entity, moduleDir);

        // Generate Java code files
        generateJavaFiles(entity, moduleDir);

        // Generate Spring Boot application class
        generateSpringBootApplicationClass(entity, moduleDir);

        // Generate resources
        generateResources(entity, moduleDir);

        // Generate enum classes
        generateEnumClasses(entity, moduleDir);

        // Update the parent pom.xml
        updateAppsParentPom(entity);

        System.out.println("Code generation completed successfully for: " + entity.getEntity());
        return true;
    }

    /**
     * Generates the module's POM file.
     *
     * @param entity The entity definition
     * @param moduleDir The module directory
     * @throws IOException If there's an error writing the file
     */
    private void generateModulePom(EntityDefinition entity, File moduleDir) throws IOException {
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);

        String content = templateProcessor.processTemplate("pom.xml.ftl", model);
        fileWriter.writeFile(new File(moduleDir, "pom.xml"), content);
    }

    /**
     * Generates all Java code files for the entity.
     *
     * @param entity The entity definition
     * @param moduleDir The module directory
     * @throws IOException If there's an error writing files
     */
    private void generateJavaFiles(EntityDefinition entity, File moduleDir) throws IOException {
        // Create base package directory structure
        String packagePath = entity.getPackageName().replace('.', '/');
        File srcDir = Paths.get(moduleDir.getPath(), "src/main/java", packagePath).toFile();
        if (!srcDir.exists()) {
            boolean created = srcDir.mkdirs();
            if (!created && !srcDir.exists()) {
                throw new IOException("Failed to create source directory: " + srcDir.getAbsolutePath());
            }
        }

        // Create model, service, repository, web directories
        createDirectories(srcDir, "model", "service", "repository", "web", "web/dto");

        // Common model for all templates
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);

        // Generate model files
        generateFile(entity, moduleDir, "model/Entity.java.ftl",
                entity.getEntity() + ".java");
        generateFile(entity, moduleDir, "model/EntityImpl.java.ftl",
                entity.getEntity() + "Impl.java");
        generateFile(entity, moduleDir, "model/EntityWrapper.java.ftl",
                entity.getEntity() + "Wrapper.java");

        // Generate service files
        generateFile(entity, moduleDir, "service/Service.java.ftl",
                entity.getEntity() + "Service.java");
        generateFile(entity, moduleDir, "service/ServiceImpl.java.ftl",
                entity.getEntity() + "ServiceImpl.java");

        // Generate repository files
        generateFile(entity, moduleDir, "repository/Repository.java.ftl",
                entity.getEntity() + "Repository.java");

        // Generate web files
        if (entity.isGenerateController()) {
            // Generate controller and DTOs
            generateFile(entity, moduleDir, "web/Controller.java.ftl",
                    entity.getEntity() + "Controller.java");
            generateFile(entity, moduleDir, "web/dto/BaseDTO.java.ftl",
                    entity.getEntity() + "DTO.java");
            generateFile(entity, moduleDir, "web/dto/RequestDTO.java.ftl",
                    entity.getEntity() + "RequestDTO.java");
            generateFile(entity, moduleDir, "web/dto/ResponseDTO.java.ftl",
                    entity.getEntity() + "ResponseDTO.java");
        }
    }

    /**
     * Generates the Spring Boot application class.
     *
     * @param entity The entity definition
     * @param moduleDir The module directory
     * @throws IOException If there's an error writing the file
     */
    private void generateSpringBootApplicationClass(EntityDefinition entity, File moduleDir) throws IOException {
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);

        String content = templateProcessor.processTemplate("Application.java.ftl", model);

        // Create the output directory
        String packagePath = entity.getPackageName().replace('.', '/');
        File outputDir = Paths.get(moduleDir.getPath(), "src/main/java", packagePath).toFile();

        fileWriter.writeFile(new File(outputDir, entity.getEntity() + "Application.java"), content);
    }

    /**
     * Generates resource files for the entity.
     *
     * @param entity The entity definition
     * @param moduleDir The module directory
     * @throws IOException If there's an error writing files
     */
    private void generateResources(EntityDefinition entity, File moduleDir) throws IOException {
        // Create resources directories
        File resourcesDir = Paths.get(moduleDir.getPath(), "src/main/resources").toFile();
        if (!resourcesDir.exists()) {
            boolean created = resourcesDir.mkdirs();
            if (!created && !resourcesDir.exists()) {
                throw new IOException("Failed to create resources directory: " + resourcesDir.getAbsolutePath());
            }
        }

        File migrationsDir = Paths.get(resourcesDir.getPath(), "db/migration").toFile();
        if (!migrationsDir.exists()) {
            boolean created = migrationsDir.mkdirs();
            if (!created && !migrationsDir.exists()) {
                throw new IOException("Failed to create migrations directory: " + migrationsDir.getAbsolutePath());
            }
        }

        File springDir = Paths.get(resourcesDir.getPath(), "META-INF/spring").toFile();
        if (!springDir.exists()) {
            boolean created = springDir.mkdirs();
            if (!created && !springDir.exists()) {
                throw new IOException("Failed to create Spring META-INF directory: " + springDir.getAbsolutePath());
            }
        }

        // Generate database migration
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);

        String migrationContent = templateProcessor.processTemplate("sql/migration.sql.ftl", model);
        fileWriter.writeFile(new File(migrationsDir, "V1__create_" + entity.getTable() + ".sql"),
                migrationContent);

        // Generate Spring configuration
        String springConfig = templateProcessor.processTemplate("module-spring.xml.ftl", model);
        fileWriter.writeFile(new File(springDir, "module-spring.xml"), springConfig);

        // Generate minimal application.yml
        String appYmlContent = templateProcessor.processTemplate("application.yml.ftl", model);
        fileWriter.writeFile(new File(resourcesDir, "application.yml"), appYmlContent);
    }

    /**
     * Generates a single file from a template.
     *
     * @param entity The entity definition
     * @param moduleDir The module directory
     * @param templateName The template name
     * @param outputFileName The output file name
     * @throws IOException If there's an error processing the template or writing the file
     */
    private void generateFile(EntityDefinition entity, File moduleDir, String templateName,
                              String outputFileName) throws IOException {
        Map<String, Object> model = new HashMap<>();
        model.put("entity", entity);

        String content = templateProcessor.processTemplate(templateName, model);

        // Determine the output directory based on the template path
        String packagePath = entity.getPackageName().replace('.', '/');
        String templateDir = templateName.substring(0, templateName.lastIndexOf('/'));
        File outputDir = Paths.get(moduleDir.getPath(), "src/main/java", packagePath, templateDir).toFile();

        fileWriter.writeFile(new File(outputDir, outputFileName), content);
    }

    /**
     * Generates enum classes defined in the entity.
     *
     * @param entity The entity definition
     * @param moduleDir The module directory
     * @throws IOException If there's an error writing files
     */
    private void generateEnumClasses(EntityDefinition entity, File moduleDir) throws IOException {
        Map<String, List<String>> enumClasses = new HashMap<>();

        // Collect all enums defined in the entity
        for (FieldDefinition field : entity.getFields()) {
            if (field.isEnum()) {
                enumClasses.put(field.getEnumClass(), field.getValues());
            }
        }

        // Create enums directory
        String packagePath = entity.getPackageName().replace('.', '/');
        File enumsDir = Paths.get(moduleDir.getPath(), "src/main/java", packagePath, "model/enums").toFile();
        if (!enumsDir.exists()) {
            boolean created = enumsDir.mkdirs();
            if (!created && !enumsDir.exists()) {
                throw new IOException("Failed to create enums directory: " + enumsDir.getAbsolutePath());
            }
        }

        // Generate enum class for each unique enum
        for (Map.Entry<String, List<String>> entry : enumClasses.entrySet()) {
            String enumClassName = entry.getKey();
            List<String> enumValues = entry.getValue();

            // Create the model for the template
            Map<String, Object> model = new HashMap<>();
            model.put("packageName", entity.getPackageName() + ".model.enums");
            model.put("enumClass", enumClassName);
            model.put("enumValues", enumValues);

            // Process the template
            String content = templateProcessor.processTemplate("model/enums/Enum.java.ftl", model);

            // Write the enum file
            fileWriter.writeFile(new File(enumsDir, enumClassName + ".java"), content);
        }
    }

    /**
     * Updates the parent apps/pom.xml to include the new module.
     *
     * @param entity The entity definition
     * @throws IOException If there's an error reading or writing the pom.xml
     */
    private void updateAppsParentPom(EntityDefinition entity) throws IOException {
        File appsDir = new File(outputDirectory);
        File pomFile = new File(appsDir, "pom.xml");

        if (!pomFile.exists()) {
            System.err.println("Warning: Parent pom.xml not found at " + pomFile.getAbsolutePath());
            return;
        }

        // Read the current pom.xml content
        String pomContent = new String(Files.readAllBytes(pomFile.toPath()), StandardCharsets.UTF_8);

        // Check if the module is already included
        String moduleName = entity.getModule();
        if (pomContent.contains("<module>" + moduleName + "</module>")) {
            System.out.println("Module " + moduleName + " already included in parent pom.xml");
            return;
        }

        // Find the modules section
        int modulesStart = pomContent.indexOf("<modules>");
        int modulesEnd = pomContent.indexOf("</modules>");

        StringBuilder updatedPom = new StringBuilder(pomContent);
        boolean moduleAdded = false;

        if (modulesStart != -1 && modulesEnd != -1) {
            // Modules section exists, add our module before the closing tag
            String moduleEntry = "        <module>" + moduleName + "</module>\n    ";
            updatedPom.insert(modulesEnd, moduleEntry);
            moduleAdded = true;
        } else {
            // No modules section found, add one before </project>
            int projectEnd = pomContent.lastIndexOf("</project>");
            if (projectEnd != -1) {
                String moduleSection =
                        "    <modules>\n" +
                                "        <module>" + moduleName + "</module>\n" +
                                "    </modules>\n\n";
                updatedPom.insert(projectEnd, moduleSection);
                moduleAdded = true;
            }
        }

        if (moduleAdded) {
            // Write the updated pom.xml
            Files.write(pomFile.toPath(), updatedPom.toString().getBytes(StandardCharsets.UTF_8));
            System.out.println("Updated parent pom.xml to include module: " + moduleName);

            // Provide instructions for refreshing Maven
            System.out.println("\nIMPORTANT: You need to refresh your Maven project to detect the new module.");
            System.out.println("In Eclipse: Right-click project > Maven > Update Project");
            System.out.println("In IntelliJ: Right-click pom.xml > Maven > Reload Project");
            System.out.println("From command line: Run 'mvn clean install' in the parent directory");
        } else {
            System.err.println("Warning: Could not update parent pom.xml to include module: " + moduleName);
            System.err.println("Please manually add the module to the parent pom.xml");
        }
    }

    /**
     * Creates multiple directories under a parent directory.
     *
     * @param parent The parent directory
     * @param dirs The directory names to create
     */
    private void createDirectories(File parent, String... dirs) {
        for (String dir : dirs) {
            File directory = new File(parent, dir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created && !directory.exists()) {
                    System.err.println("Warning: Failed to create directory: " + directory.getAbsolutePath());
                }
            }
        }
    }
}