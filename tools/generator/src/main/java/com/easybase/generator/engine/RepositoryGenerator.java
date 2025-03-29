package com.easybase.generator.engine;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.template.TemplateContext;
import com.easybase.generator.template.TemplateProcessor;

import java.io.IOException;

/**
 * Generates repository classes for an entity.
 */
public class RepositoryGenerator extends BaseGenerator {

    /**
     * Constructs a new RepositoryGenerator.
     *
     * @param templateProcessor The template processor
     * @param fileManager The file manager
     * @param options The generation options
     */
    public RepositoryGenerator(TemplateProcessor templateProcessor, FileManager fileManager, GenerationOptions options) {
        super(templateProcessor, fileManager, options);
    }

    @Override
    public void generate(EntityDefinition entity) throws IOException {
        // Generate base repository interface
        generateBaseRepository(entity);

        // Generate custom repository interface (if it doesn't exist)
        generateCustomRepository(entity);
    }

    /**
     * Generates the base repository interface.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateBaseRepository(EntityDefinition entity) throws IOException {
        String basePackagePath = getBaseComponentPackagePath(entity, "repository");
        String baseClassName = entity.getName() + "BaseJpaRepository";
        String filePath = getJavaFilePath(basePackagePath, baseClassName);

        TemplateContext context = createTemplateContext(entity);
        String content = templateProcessor.process("repository/BaseRepository.ftl", context);

        fileManager.writeFile(filePath, content, true);
    }

    /**
     * Generates the custom repository interface.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateCustomRepository(EntityDefinition entity) throws IOException {
        String packagePath = getComponentPackagePath(entity, "repository");
        String className = entity.getName() + "JpaRepository";
        String filePath = getJavaFilePath(packagePath, className);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(filePath)) {
            TemplateContext context = createTemplateContext(entity);
            String content = templateProcessor.process("repository/CustomRepository.ftl", context);

            fileManager.writeFile(filePath, content, false);
        }
    }
}