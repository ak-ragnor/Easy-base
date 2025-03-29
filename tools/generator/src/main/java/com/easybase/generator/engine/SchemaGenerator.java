package com.easybase.generator.engine;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.model.FieldDefinition;
import com.easybase.generator.model.RelationshipDefinition;
import com.easybase.generator.template.TemplateContext;
import com.easybase.generator.template.TemplateProcessor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates database schema for an entity.
 */
public class SchemaGenerator extends BaseGenerator {

    /**
     * Constructs a new SchemaGenerator.
     *
     * @param templateProcessor The template processor
     * @param fileManager The file manager
     * @param options The generation options
     */
    public SchemaGenerator(TemplateProcessor templateProcessor, FileManager fileManager, GenerationOptions options) {
        super(templateProcessor, fileManager, options);
    }

    @Override
    public void generate(EntityDefinition entity) throws IOException {
        // Generate create table migration
        generateCreateTableMigration(entity);

        // Generate schema-related Spring configuration
        generateSchemaConfiguration(entity);
    }

    /**
     * Generates the create table migration script.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateCreateTableMigration(EntityDefinition entity) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String migrationName = "V" + timestamp + "__create_" + entity.getTable() + ".sql";
        String migrationPath = getResourceFilePath("db/migration/" + migrationName);

        TemplateContext context = createTemplateContext(entity);
        String content = templateProcessor.process("schema/migration.ftl", context);

        fileManager.writeFile(migrationPath, content, true);
    }

    /**
     * Generates schema-related Spring configuration.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateSchemaConfiguration(EntityDefinition entity) throws IOException {
        String configName = entity.getName() + "SchemaConfig.java";
        String configPath = getJavaFilePath(getComponentPackagePath(entity, "infrastructure/config"), configName);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(configPath)) {
            TemplateContext context = createTemplateContext(entity);
            String content = templateProcessor.process("config/SchemaConfig.ftl", context);

            fileManager.writeFile(configPath, content, false);
        }
    }
}