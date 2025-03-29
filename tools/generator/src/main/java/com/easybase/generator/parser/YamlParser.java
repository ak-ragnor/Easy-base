package com.easybase.generator.parser;

import com.easybase.generator.model.*;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parser for entity definition YAML files.
 */
public class YamlParser {

    private final EntityParser entityParser;
    private final FieldParser fieldParser;
    private final RelationshipParser relationshipParser;

    /**
     * Constructs a new YamlParser with its dependencies.
     */
    public YamlParser() {
        this.entityParser = new EntityParser();
        this.fieldParser = new FieldParser();
        this.relationshipParser = new RelationshipParser();
    }

    /**
     * Parses a YAML file into a list of EntityDefinition objects.
     *
     * @param file The YAML file to parse
     * @return List of EntityDefinition objects
     * @throws IOException If an error occurs reading the file
     */
    public List<EntityDefinition> parse(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            return parse(inputStream);
        }
    }

    /**
     * Parses YAML content from an input stream into a list of EntityDefinition objects.
     *
     * @param inputStream The input stream containing YAML content
     * @return List of EntityDefinition objects
     */
    public List<EntityDefinition> parse(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> config = yaml.load(inputStream);

        return parseConfig(config);
    }

    /**
     * Parses a configuration map into a list of EntityDefinition objects.
     *
     * @param config The configuration map
     * @return List of EntityDefinition objects
     */
    @SuppressWarnings("unchecked")
    private List<EntityDefinition> parseConfig(Map<String, Object> config) {
        List<EntityDefinition> entities = new ArrayList<>();

        // Get module-level properties
        String packageName = (String) config.getOrDefault("package", "");

        // Parse entities
        List<Map<String, Object>> entityMaps = (List<Map<String, Object>>) config.get("entities");
        if (entityMaps != null) {
            for (Map<String, Object> entityMap : entityMaps) {
                // Add module package as default if not specified
                if (!entityMap.containsKey("package") && !packageName.isEmpty()) {
                    entityMap.put("package", packageName);
                }

                EntityDefinition entity = entityParser.parse(entityMap);

                // Parse fields
                if (entityMap.containsKey("fields")) {
                    List<Map<String, Object>> fieldMaps = (List<Map<String, Object>>) entityMap.get("fields");
                    List<FieldDefinition> fields = fieldParser.parse(fieldMaps);
                    entity.setFields(fields);
                }

                entities.add(entity);
            }
        }

        // Parse relationships (if defined at the module level)
        if (config.containsKey("relationships")) {
            List<Map<String, Object>> relationshipMaps = (List<Map<String, Object>>) config.get("relationships");
            List<RelationshipDefinition> relationships = relationshipParser.parse(relationshipMaps);

            // Assign relationships to the appropriate entities
            for (RelationshipDefinition relationship : relationships) {
                for (EntityDefinition entity : entities) {
                    if (entity.getName().equals(relationship.getFromEntity())) {
                        entity.getRelationships().add(relationship);
                    }
                }
            }
        }

        return entities;
    }
}