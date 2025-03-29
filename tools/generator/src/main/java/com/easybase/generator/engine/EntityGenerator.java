package com.easybase.generator.engine;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.template.TemplateProcessor;

import java.io.IOException;
import java.util.List;

/**
 * Coordinates the generation of all code for an entity.
 */
public class EntityGenerator extends BaseGenerator {

    private final ModelGenerator modelGenerator;
    private final RepositoryGenerator repositoryGenerator;
    private final ServiceGenerator serviceGenerator;
    private final ApiGenerator apiGenerator;
    private final SchemaGenerator schemaGenerator;
    private final TestGenerator testGenerator;

    /**
     * Constructs a new EntityGenerator.
     *
     * @param templateProcessor The template processor
     * @param fileManager The file manager
     * @param options The generation options
     */
    public EntityGenerator(TemplateProcessor templateProcessor, FileManager fileManager, GenerationOptions options) {
        super(templateProcessor, fileManager, options);

        this.modelGenerator = new ModelGenerator(templateProcessor, fileManager, options);
        this.repositoryGenerator = new RepositoryGenerator(templateProcessor, fileManager, options);
        this.serviceGenerator = new ServiceGenerator(templateProcessor, fileManager, options);
        this.apiGenerator = new ApiGenerator(templateProcessor, fileManager, options);
        this.schemaGenerator = new SchemaGenerator(templateProcessor, fileManager, options);
        this.testGenerator = new TestGenerator(templateProcessor, fileManager, options);
    }

    /**
     * Generates code for all entities.
     *
     * @param entities The entities to generate code for
     * @throws IOException If an I/O error occurs
     */
    public void generateAll(List<EntityDefinition> entities) throws IOException {
        for (EntityDefinition entity : entities) {
            generate(entity);
        }
    }

    @Override
    public void generate(EntityDefinition entity) throws IOException {
        // Generate model classes
        modelGenerator.generate(entity);

        // Generate repository classes
        repositoryGenerator.generate(entity);

        // Generate service classes
        serviceGenerator.generate(entity);

        // Generate API classes
        apiGenerator.generate(entity);

        // Generate database schema
        schemaGenerator.generate(entity);

        // Generate test classes
        if (options.isGenerateTests()) {
            testGenerator.generate(entity);
        }
    }
}