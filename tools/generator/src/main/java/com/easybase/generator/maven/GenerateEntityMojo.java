package com.easybase.generator.maven;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.engine.EntityGenerator;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.model.FieldDefinition;
import com.easybase.generator.template.TemplateProcessor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;

/**
 * Maven plugin for generating a single entity from Maven parameters.
 * This mojo implements the "generate-entity" goal.
 */
@Mojo(name = "generate-entity", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateEntityMojo extends AbstractMojo {

    /**
     * The entity name.
     */
    @Parameter(property = "entity", required = true)
    private String entityName;

    /**
     * The database table name.
     */
    @Parameter(property = "table")
    private String tableName;

    /**
     * The package name.
     */
    @Parameter(property = "package", defaultValue = "com.easybase")
    private String packageName;

    /**
     * The output directory for generated code.
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.basedir}")
    private String outputDirectory;

    /**
     * Whether to generate test classes.
     */
    @Parameter(property = "generateTests", defaultValue = "true")
    private boolean generateTests;

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating entity: " + entityName);

        try {
            EntityDefinition entity = createEntityDefinition();

            GenerationOptions options = GenerationOptions.builder()
                    .outputDirectory(outputDirectory)
                    .generateTests(generateTests)
                    .build();

            FileManager fileManager = new FileManager(outputDirectory);
            TemplateProcessor templateProcessor = new TemplateProcessor();

            EntityGenerator generator = new EntityGenerator(templateProcessor, fileManager, options);

            generator.generate(entity);

            getLog().info("Entity generation completed successfully: " + entityName);
        } catch (Exception e) {
            throw new MojoExecutionException("Error generating entity", e);
        }
    }

    /**
     * Creates an entity definition from the provided parameters.
     */
    private EntityDefinition createEntityDefinition() {
        if (tableName == null || tableName.isEmpty()) {
            tableName = "eb_" + entityName.toLowerCase();
        }

        EntityDefinition.Builder builder = EntityDefinition.builder()
                .withName(entityName)
                .withTable(tableName)
                .withPackage(packageName);

        List<FieldDefinition> fields = new ArrayList<>();

        FieldDefinition idField = FieldDefinition.builder()
                .withName("id")
                .withType("UUID")
                .withPrimaryKey(true)
                .withGenerated(true)
                .build();
        fields.add(idField);

        FieldDefinition nameField = FieldDefinition.builder()
                .withName("name")
                .withType("String")
                .withLength(100)
                .withNullable(false)
                .build();
        fields.add(nameField);

        FieldDefinition descField = FieldDefinition.builder()
                .withName("description")
                .withType("String")
                .withLength(500)
                .build();
        fields.add(descField);

        FieldDefinition activeField = FieldDefinition.builder()
                .withName("active")
                .withType("Boolean")
                .withDefaultValue(true)
                .build();
        fields.add(activeField);

        builder.withFields(fields);

        EntityDefinition.AuditConfig auditConfig = new EntityDefinition.AuditConfig();
        auditConfig.setEnabled(true);
        List<String> auditFields = new ArrayList<>();
        auditFields.add("createdDate");
        auditFields.add("modifiedDate");
        auditFields.add("version");
        auditConfig.setFields(auditFields);

        builder.withAuditConfig(auditConfig);

        return builder.build();
    }
}