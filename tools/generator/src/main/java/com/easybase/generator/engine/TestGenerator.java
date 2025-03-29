package com.easybase.generator.engine;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.template.TemplateContext;
import com.easybase.generator.template.TemplateProcessor;

import java.io.IOException;

/**
 * Generates test classes for an entity.
 */
public class TestGenerator extends BaseGenerator {

    /**
     * Constructs a new TestGenerator.
     *
     * @param templateProcessor The template processor
     * @param fileManager The file manager
     * @param options The generation options
     */
    public TestGenerator(TemplateProcessor templateProcessor, FileManager fileManager, GenerationOptions options) {
        super(templateProcessor, fileManager, options);
    }

    @Override
    public void generate(EntityDefinition entity) throws IOException {
        // Generate repository test
        generateRepositoryTest(entity);

        // Generate service test
        generateServiceTest(entity);

        // Generate controller test
        generateControllerTest(entity);
    }

    /**
     * Generates the repository test class.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateRepositoryTest(EntityDefinition entity) throws IOException {
        String packagePath = getComponentPackagePath(entity, "repository") + "/test";
        String className = entity.getName() + "RepositoryTest";
        String filePath = getTestFilePath(packagePath, className);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(filePath)) {
            TemplateContext context = createTemplateContext(entity);
            String content = templateProcessor.process("test/RepositoryTest.ftl", context);

            fileManager.writeFile(filePath, content, false);
        }
    }

    /**
     * Generates the service test class.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateServiceTest(EntityDefinition entity) throws IOException {
        String packagePath = getComponentPackagePath(entity, "service") + "/test";
        String className = entity.getName() + "ServiceTest";
        String filePath = getTestFilePath(packagePath, className);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(filePath)) {
            TemplateContext context = createTemplateContext(entity);
            String content = templateProcessor.process("test/ServiceTest.ftl", context);

            fileManager.writeFile(filePath, content, false);
        }
    }

    /**
     * Generates the controller test class.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateControllerTest(EntityDefinition entity) throws IOException {
        String packagePath = getComponentPackagePath(entity, "api") + "/test";
        String className = entity.getName() + "ControllerTest";
        String filePath = getTestFilePath(packagePath, className);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(filePath)) {
            TemplateContext context = createTemplateContext(entity);
            String content = templateProcessor.process("test/ControllerTest.ftl", context);

            fileManager.writeFile(filePath, content, false);
        }
    }
}