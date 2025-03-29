package com.easybase.generator.engine;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.template.TemplateContext;
import com.easybase.generator.template.TemplateProcessor;

import java.io.IOException;
import java.util.Map;

/**
 * Base class for code generators.
 */
public abstract class BaseGenerator {

    protected final TemplateProcessor templateProcessor;
    protected final FileManager fileManager;
    protected final GenerationOptions options;

    /**
     * Constructs a new BaseGenerator.
     *
     * @param templateProcessor The template processor
     * @param fileManager The file manager
     * @param options The generation options
     */
    public BaseGenerator(TemplateProcessor templateProcessor, FileManager fileManager, GenerationOptions options) {
        this.templateProcessor = templateProcessor;
        this.fileManager = fileManager;
        this.options = options;
    }

    /**
     * Generates code for an entity.
     *
     * @param entity The entity to generate code for
     * @throws IOException If an I/O error occurs
     */
    public abstract void generate(EntityDefinition entity) throws IOException;

    /**
     * Creates a template context for an entity.
     *
     * @param entity The entity
     * @return The template context
     */
    protected TemplateContext createTemplateContext(EntityDefinition entity) {
        TemplateContext context = TemplateContext.create();
        context.set("entity", entity);
        context.set("options", options);
        return context;
    }

    /**
     * Creates a template context for an entity with additional parameters.
     *
     * @param entity The entity
     * @param params Additional parameters
     * @return The template context
     */
    protected TemplateContext createTemplateContext(EntityDefinition entity, Map<String, Object> params) {
        TemplateContext context = createTemplateContext(entity);
        context.setAll(params);
        return context;
    }

    /**
     * Gets the component package path.
     *
     * @param entity The entity
     * @param componentType The component type
     * @return The package path
     */
    protected String getComponentPackagePath(EntityDefinition entity, String componentType) {
        return entity.getComponentPackage(componentType).replace('.', '/');
    }

    /**
     * Gets the base component package path.
     *
     * @param entity The entity
     * @param componentType The component type
     * @return The package path
     */
    protected String getBaseComponentPackagePath(EntityDefinition entity, String componentType) {
        return entity.getBaseComponentPackage(componentType).replace('.', '/');
    }

    /**
     * Gets a file path for a Java file.
     *
     * @param packagePath The package path
     * @param className The class name
     * @return The file path
     */
    protected String getJavaFilePath(String packagePath, String className) {
        return "src/main/java/" + packagePath + "/" + className + ".java";
    }

    /**
     * Gets a file path for a resource file.
     *
     * @param resourcePath The resource path
     * @return The file path
     */
    protected String getResourceFilePath(String resourcePath) {
        return "src/main/resources/" + resourcePath;
    }

    /**
     * Gets a file path for a test file.
     *
     * @param packagePath The package path
     * @param className The class name
     * @return The file path
     */
    protected String getTestFilePath(String packagePath, String className) {
        return "src/test/java/" + packagePath + "/" + className + ".java";
    }
}