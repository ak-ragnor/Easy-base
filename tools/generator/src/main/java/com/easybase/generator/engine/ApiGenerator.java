package com.easybase.generator.engine;

import com.easybase.generator.config.GenerationOptions;
import com.easybase.generator.io.FileManager;
import com.easybase.generator.model.DtoLevel;
import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.template.TemplateContext;
import com.easybase.generator.template.TemplateProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates API classes for an entity.
 */
public class ApiGenerator extends BaseGenerator {

    /**
     * Constructs a new ApiGenerator.
     *
     * @param templateProcessor The template processor
     * @param fileManager The file manager
     * @param options The generation options
     */
    public ApiGenerator(TemplateProcessor templateProcessor, FileManager fileManager, GenerationOptions options) {
        super(templateProcessor, fileManager, options);
    }

    @Override
    public void generate(EntityDefinition entity) throws IOException {
        // Generate DTOs
        generateDtos(entity);

        // Generate mappers
        generateMappers(entity);

        // Generate base controller
        generateBaseController(entity);

        // Generate custom controller (if it doesn't exist)
        generateCustomController(entity);

        // Generate validators
        generateValidators(entity);
    }

    /**
     * Generates DTO classes.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateDtos(EntityDefinition entity) throws IOException {
        String basePackagePath = getBaseComponentPackagePath(entity, "api") + "/model/dto";

        // Generate base DTO
        String baseDtoName = entity.getName() + "BaseDTO";
        String baseDtoPath = getJavaFilePath(basePackagePath, baseDtoName);

        TemplateContext baseDtoContext = createTemplateContext(entity);
        String baseDtoContent = templateProcessor.process("api/dto/BaseDTO.ftl", baseDtoContext);

        fileManager.writeFile(baseDtoPath, baseDtoContent, true);

        // Generate request DTO
        String requestDtoName = entity.getName() + "RequestDTO";
        String requestDtoPath = getJavaFilePath(basePackagePath, requestDtoName);

        TemplateContext requestDtoContext = createTemplateContext(entity);
        String requestDtoContent = templateProcessor.process("api/dto/RequestDTO.ftl", requestDtoContext);

        fileManager.writeFile(requestDtoPath, requestDtoContent, true);

        // Generate response DTO
        String responseDtoName = entity.getName() + "ResponseDTO";
        String responseDtoPath = getJavaFilePath(basePackagePath, responseDtoName);

        TemplateContext responseDtoContext = createTemplateContext(entity);
        String responseDtoContent = templateProcessor.process("api/dto/ResponseDTO.ftl", responseDtoContext);

        fileManager.writeFile(responseDtoPath, responseDtoContent, true);

        // Generate DTOs for each level (if specified)
        for (DtoLevel level : entity.getDtoLevels()) {
            String levelDtoName = level.getDtoClassName(entity.getName());
            if (levelDtoName.equals(entity.getName() + "DTO")) {
                // Skip default DTO
                continue;
            }

            String levelDtoPath = getJavaFilePath(basePackagePath, levelDtoName);

            Map<String, Object> params = new HashMap<>();
            params.put("dtoLevel", level);

            TemplateContext levelDtoContext = createTemplateContext(entity, params);
            String levelDtoContent = templateProcessor.process("api/dto/LevelDTO.ftl", levelDtoContext);

            fileManager.writeFile(levelDtoPath, levelDtoContent, true);
        }

        // Generate custom DTO classes (only if they don't exist)
        String packagePath = getComponentPackagePath(entity, "api") + "/model/dto";
        String dtoName = entity.getName() + "DTO";
        String dtoPath = getJavaFilePath(packagePath, dtoName);

        if (!fileManager.fileExists(dtoPath)) {
            TemplateContext dtoContext = createTemplateContext(entity);
            String dtoContent = templateProcessor.process("api/dto/CustomDTO.ftl", dtoContext);

            fileManager.writeFile(dtoPath, dtoContent, false);
        }
    }

    /**
     * Generates mapper classes.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateMappers(EntityDefinition entity) throws IOException {
        String basePackagePath = getBaseComponentPackagePath(entity, "api") + "/model/mapper";

        // Generate base mapper
        String baseMapperName = entity.getName() + "BaseMapper";
        String baseMapperPath = getJavaFilePath(basePackagePath, baseMapperName);

        TemplateContext baseMapperContext = createTemplateContext(entity);
        String baseMapperContent = templateProcessor.process("api/mapper/BaseMapper.ftl", baseMapperContext);

        fileManager.writeFile(baseMapperPath, baseMapperContent, true);

        // Generate custom mapper (if it doesn't exist)
        String packagePath = getComponentPackagePath(entity, "api") + "/model/mapper";
        String mapperName = entity.getName() + "Mapper";
        String mapperPath = getJavaFilePath(packagePath, mapperName);

        if (!fileManager.fileExists(mapperPath)) {
            TemplateContext mapperContext = createTemplateContext(entity);
            String mapperContent = templateProcessor.process("api/mapper/CustomMapper.ftl", mapperContext);

            fileManager.writeFile(mapperPath, mapperContent, false);
        }
    }

    /**
     * Generates the base controller class.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateBaseController(EntityDefinition entity) throws IOException {
        String basePackagePath = getBaseComponentPackagePath(entity, "api");
        String baseControllerName = entity.getName() + "BaseController";
        String baseControllerPath = getJavaFilePath(basePackagePath, baseControllerName);

        TemplateContext baseControllerContext = createTemplateContext(entity);
        String baseControllerContent = templateProcessor.process("api/controller/BaseController.ftl", baseControllerContext);

        fileManager.writeFile(baseControllerPath, baseControllerContent, true);
    }

    /**
     * Generates the custom controller class.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateCustomController(EntityDefinition entity) throws IOException {
        String packagePath = getComponentPackagePath(entity, "api");
        String controllerName = entity.getName() + "Controller";
        String controllerPath = getJavaFilePath(packagePath, controllerName);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(controllerPath)) {
            TemplateContext controllerContext = createTemplateContext(entity);
            String controllerContent = templateProcessor.process("api/controller/CustomController.ftl", controllerContext);

            fileManager.writeFile(controllerPath, controllerContent, false);
        }
    }

    /**
     * Generates validator classes.
     *
     * @param entity The entity definition
     * @throws IOException If an I/O error occurs
     */
    private void generateValidators(EntityDefinition entity) throws IOException {
        String packagePath = getComponentPackagePath(entity, "api") + "/validator";
        String validatorName = entity.getName() + "Validator";
        String validatorPath = getJavaFilePath(packagePath, validatorName);

        // Only generate if the file doesn't exist
        if (!fileManager.fileExists(validatorPath)) {
            TemplateContext validatorContext = createTemplateContext(entity);
            String validatorContent = templateProcessor.process("api/validator/Validator.ftl", validatorContext);

            fileManager.writeFile(validatorPath, validatorContent, false);
        }
    }
}