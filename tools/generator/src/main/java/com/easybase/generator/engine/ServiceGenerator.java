package com.easybase.generator.engine;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.template.TemplateContext;
import com.easybase.generator.template.TemplateProcessor;

import java.io.IOException;

/**
 * Generates service classes for an entity.
 */
public class ServiceGenerator extends BaseGenerator {

    /**
     * Constructs a new ServiceGenerator.
     *
     * @param templateProcessor The template processor
     * @param fileManager The file manager
     * @param options The generation options
     */
    public ServiceGenerator(TemplateProcessor templateProcessor, FileManager fileManager, GenerationOptions options) {
        super(templateProcessor, fileManager, options);
    }

    @Override
    public void generate(EntityDefinition entity) throws IOException {
        // Generate base local service
        generateBaseLocalService(entity);

        // Generate custom local service (if it doesn't exist)
        generateCustomLocalService(entity);

        // Generate base service
        generateBaseService(entity);

        // Generate custom service (if it doesn't exist)
        generateCustomService(entity);
    }

    /**
     * Generates the base local service interface and implementation.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateBaseLocalService(EntityDefinition entity) throws IOException {
        String basePackagePath = getBaseComponentPackagePath(entity, "service");

        // Generate interface
        String baseInterfaceName = entity.getName() + "BaseLocalService";
        String interfacePath = getJavaFilePath(basePackagePath, baseInterfaceName);

        TemplateContext interfaceContext = createTemplateContext(entity);
        String interfaceContent = templateProcessor.process("service/BaseLocalService.ftl", interfaceContext);

        fileManager.writeFile(interfacePath, interfaceContent, true);

        // Generate implementation
        String baseImplName = entity.getName() + "BaseLocalServiceImpl";
        String implPath = getJavaFilePath(basePackagePath, baseImplName);

        TemplateContext implContext = createTemplateContext(entity);
        String implContent = templateProcessor.process("service/BaseLocalServiceImpl.ftl", implContext);

        fileManager.writeFile(implPath, implContent, true);
    }

    /**
     * Generates the custom local service interface and implementation.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateCustomLocalService(EntityDefinition entity) throws IOException {
        String packagePath = getComponentPackagePath(entity, "service");

        // Generate interface
        String interfaceName = entity.getName() + "LocalService";
        String interfacePath = getJavaFilePath(packagePath, interfaceName);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(interfacePath)) {
            TemplateContext interfaceContext = createTemplateContext(entity);
            String interfaceContent = templateProcessor.process("service/CustomLocalService.ftl", interfaceContext);

            fileManager.writeFile(interfacePath, interfaceContent, false);
        }

        // Generate implementation
        String implName = entity.getName() + "LocalServiceImpl";
        String implPath = getJavaFilePath(packagePath, implName);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(implPath)) {
            TemplateContext implContext = createTemplateContext(entity);
            String implContent = templateProcessor.process("service/CustomLocalServiceImpl.ftl", implContext);

            fileManager.writeFile(implPath, implContent, false);
        }
    }

    /**
     * Generates the base service interface and implementation.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateBaseService(EntityDefinition entity) throws IOException {
        String basePackagePath = getBaseComponentPackagePath(entity, "service");

        // Generate interface
        String baseInterfaceName = entity.getName() + "BaseService";
        String interfacePath = getJavaFilePath(basePackagePath, baseInterfaceName);

        TemplateContext interfaceContext = createTemplateContext(entity);
        String interfaceContent = templateProcessor.process("service/BaseService.ftl", interfaceContext);

        fileManager.writeFile(interfacePath, interfaceContent, true);

        // Generate implementation
        String baseImplName = entity.getName() + "BaseServiceImpl";
        String implPath = getJavaFilePath(basePackagePath, baseImplName);

        TemplateContext implContext = createTemplateContext(entity);
        String implContent = templateProcessor.process("service/BaseServiceImpl.ftl", implContext);

        fileManager.writeFile(implPath, implContent, true);
    }

    /**
     * Generates the custom service interface and implementation.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateCustomService(EntityDefinition entity) throws IOException {
        String packagePath = getComponentPackagePath(entity, "service");

        // Generate interface
        String interfaceName = entity.getName() + "Service";
        String interfacePath = getJavaFilePath(packagePath, interfaceName);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(interfacePath)) {
            TemplateContext interfaceContext = createTemplateContext(entity);
            String interfaceContent = templateProcessor.process("service/CustomService.ftl", interfaceContext);

            fileManager.writeFile(interfacePath, interfaceContent, false);
        }

        // Generate implementation
        String implName = entity.getName() + "ServiceImpl";
        String implPath = getJavaFilePath(packagePath, implName);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(implPath)) {
            TemplateContext implContext = createTemplateContext(entity);
            String implContent = templateProcessor.process("service/CustomServiceImpl.ftl", implContext);

            fileManager.writeFile(implPath, implContent, false);
        }
    }
}