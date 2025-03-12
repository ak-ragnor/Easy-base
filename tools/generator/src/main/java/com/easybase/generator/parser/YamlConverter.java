package com.easybase.generator.parser;

import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.model.FieldDefinition;
import com.easybase.generator.model.FinderDefinition;
import com.easybase.generator.model.SearchConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts raw YAML maps into structured model objects.
 * This class handles the conversion of untyped maps from the YAML parser
 * into strongly-typed model objects used for code generation.
 */
public class YamlConverter {

    /**
     * Converts a raw YAML map to an EntityDefinition.
     *
     * @param yamlMap The raw YAML data as a map
     * @return The converted EntityDefinition
     */
    public EntityDefinition convert(Map<String, Object> yamlMap) {
        EntityDefinition entity = new EntityDefinition();

        // Set basic properties
        entity.setModule((String) yamlMap.get("module"));
        entity.setEntity((String) yamlMap.get("entity"));
        entity.setTable((String) yamlMap.get("table"));
        entity.setPackageName((String) yamlMap.get("packageName"));

        if (yamlMap.containsKey("generateController")) {
            entity.setGenerateController((Boolean) yamlMap.get("generateController"));
        }

        // Convert fields
        if (yamlMap.containsKey("fields")) {
            List<Map<String, Object>> fieldMaps = (List<Map<String, Object>>) yamlMap.get("fields");
            List<FieldDefinition> fields = new ArrayList<>();

            for (Map<String, Object> fieldMap : fieldMaps) {
                fields.add(convertField(fieldMap));
            }

            entity.setFields(fields);
        }

        // Convert finders
        if (yamlMap.containsKey("finders")) {
            List<Map<String, Object>> finderMaps = (List<Map<String, Object>>) yamlMap.get("finders");
            List<FinderDefinition> finders = new ArrayList<>();

            for (Map<String, Object> finderMap : finderMaps) {
                finders.add(convertFinder(finderMap));
            }

            entity.setFinders(finders);
        }

        return entity;
    }

    /**
     * Converts a raw field map to a FieldDefinition.
     *
     * @param fieldMap The raw field data as a map
     * @return The converted FieldDefinition
     */
    private FieldDefinition convertField(Map<String, Object> fieldMap) {
        FieldDefinition field = new FieldDefinition();

        // Set basic properties
        field.setName((String) fieldMap.get("name"));
        field.setType((String) fieldMap.get("type"));

        // Set optional properties if they exist
        if (fieldMap.containsKey("nullable")) {
            field.setNullable((Boolean) fieldMap.get("nullable"));
        }

        if (fieldMap.containsKey("primaryKey")) {
            field.setPrimaryKey((Boolean) fieldMap.get("primaryKey"));
        }

        if (fieldMap.containsKey("generated")) {
            field.setGenerated((Boolean) fieldMap.get("generated"));
        }

        if (fieldMap.containsKey("length")) {
            field.setLength((Integer) fieldMap.get("length"));
        }

        if (fieldMap.containsKey("description")) {
            field.setDescription((String) fieldMap.get("description"));
        }

        if (fieldMap.containsKey("defaultValue")) {
            field.setDefaultValue(fieldMap.get("defaultValue").toString());
        }

        if (fieldMap.containsKey("indexed")) {
            field.setIndexed((Boolean) fieldMap.get("indexed"));
        }

        // Set relationship properties if this is a relationship
        if ("Relationship".equals(field.getType())) {
            field.setTarget((String) fieldMap.get("target"));
            String targetPackage = (String) fieldMap.get("targetPackage");
            field.setTargetPackage(targetPackage);

            // Extract module name from package if available
            if (targetPackage != null && !targetPackage.isEmpty()) {
                String[] packageParts = targetPackage.split("\\.");
                if (packageParts.length >= 3) {
                    field.setTargetModule("easybase-" + packageParts[2]);
                }
            }

            field.setRelationType((String) fieldMap.get("relationType"));
            field.setJoinColumn((String) fieldMap.get("joinColumn"));
        }

        // Set enum properties if this is an enum
        if ("Enum".equals(field.getType())) {
            field.setEnumClass((String) fieldMap.get("enumClass"));
            field.setValues((List<String>) fieldMap.get("values"));
        }

        // Set validation rules
        if (fieldMap.containsKey("validation")) {
            field.setValidation((List<Map<String, Object>>) fieldMap.get("validation"));
        }

        // Set search configuration
        if (fieldMap.containsKey("search")) {
            field.setSearch(convertSearchConfig((Map<String, Object>) fieldMap.get("search")));
        }

        return field;
    }

    /**
     * Converts a raw finder map to a FinderDefinition.
     *
     * @param finderMap The raw finder data as a map
     * @return The converted FinderDefinition
     */
    private FinderDefinition convertFinder(Map<String, Object> finderMap) {
        FinderDefinition finder = new FinderDefinition();

        finder.setName((String) finderMap.get("name"));
        finder.setReturnType((String) finderMap.get("returnType"));

        if (finderMap.containsKey("query")) {
            finder.setQuery((String) finderMap.get("query"));
        }

        // Convert parameters
        if (finderMap.containsKey("parameters")) {
            List<Map<String, Object>> paramMaps = (List<Map<String, Object>>) finderMap.get("parameters");
            List<FinderDefinition.ParameterDefinition> parameters = new ArrayList<>();

            for (Map<String, Object> paramMap : paramMaps) {
                FinderDefinition.ParameterDefinition param = new FinderDefinition.ParameterDefinition();
                param.setName((String) paramMap.get("name"));
                param.setType((String) paramMap.get("type"));
                parameters.add(param);
            }

            finder.setParameters(parameters);
        }

        return finder;
    }

    /**
     * Converts a raw search config map to a SearchConfig.
     *
     * @param searchMap The raw search config data as a map
     * @return The converted SearchConfig
     */
    private SearchConfig convertSearchConfig(Map<String, Object> searchMap) {
        SearchConfig search = new SearchConfig();

        search.setType((String) searchMap.get("type"));

        if (searchMap.containsKey("analyzer")) {
            search.setAnalyzer((String) searchMap.get("analyzer"));
        }

        if (searchMap.containsKey("fields")) {
            search.setFields((Map<String, Object>) searchMap.get("fields"));
        }

        if (searchMap.containsKey("properties")) {
            search.setProperties((Map<String, Object>) searchMap.get("properties"));
        }

        return search;
    }
}