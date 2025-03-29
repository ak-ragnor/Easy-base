package com.easybase.generator.engine;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.template.TemplateContext;
import com.easybase.generator.template.TemplateProcessor;

import java.io.IOException;

/**
 * Generates model classes for an entity.
 */
public class ModelGenerator extends BaseGenerator {

    /**
     * Constructs a new ModelGenerator.
     *
     * @param templateProcessor The template processor
     * @param fileManager The file manager
     * @param options The generation options
     */
    public ModelGenerator(TemplateProcessor templateProcessor, FileManager fileManager, GenerationOptions options) {
        super(templateProcessor, fileManager, options);
    }

    @Override
    public void generate(EntityDefinition entity) throws IOException {
        // Generate base entity class
        generateBaseEntity(entity);

        // Generate custom entity class (if it doesn't exist)
        generateCustomEntity(entity);

        // Generate enum classes
        generateEnums(entity);

        // Generate model listener
        if (!entity.getListeners().isEmpty()) {
            generateModelListener(entity);
        }
    }

    /**
     * Generates the base entity class.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateBaseEntity(EntityDefinition entity) throws IOException {
        String basePackagePath = getBaseComponentPackagePath(entity, "model");
        String baseClassName = entity.getName() + "Base";
        String filePath = getJavaFilePath(basePackagePath, baseClassName);

        TemplateContext context = createTemplateContext(entity);
        String content = templateProcessor.process("model/BaseEntity.ftl", context);

        fileManager.writeFile(filePath, content, true);
    }

    /**
     * Generates the custom entity class.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateCustomEntity(EntityDefinition entity) throws IOException {
        String packagePath = getComponentPackagePath(entity, "model");
        String className = entity.getName();
        String filePath = getJavaFilePath(packagePath, className);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(filePath)) {
            TemplateContext context = createTemplateContext(entity);
            String content = templateProcessor.process("model/CustomEntity.ftl", context);

            fileManager.writeFile(filePath, content, false);
        }
    }

    /**
     * Generates enum classes.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateEnums(EntityDefinition entity) throws IOException {
        entity.getFields().stream()
                .filter(field -> field.isEnum())
                .forEach(field -> {
                    try {
                        String enumPackagePath = getComponentPackagePath(entity, "model") + "/enums";
                        String enumClassName = field.getEnumClass();
                        String filePath = getJavaFilePath(enumPackagePath, enumClassName);

                        // Only generate if the file doesn't exist
                        if (!fileManager.fileExists(filePath)) {
                            TemplateContext context = createTemplateContext(entity);
                            context.set("enumClass", field.getEnumClass());
                            context.set("enumValues", field.getEnumValues());
                            String content = templateProcessor.process("model/Enum.ftl", context);

                            fileManager.writeFile(filePath, content, false);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error generating enum class", e);
                    }
                });
    }

    /**
     * Generates the model listener class.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateModelListener(EntityDefinition entity) throws IOException {
        String packagePath = getComponentPackagePath(entity, "model") + "/listener";
        String className = entity.getName() + "ModelListener";
        String filePath = getJavaFilePath(packagePath, className);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(filePath)) {
            TemplateContext context = createTemplateContext(entity);
            String content = templateProcessor.process("model/ModelListener.ftl", context);

            fileManager.writeFile(filePath, content, false);
        }
    }
}