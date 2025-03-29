package com.easybase.generator.parser;

import com.easybase.generator.model.RelationshipDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parser for relationship definitions.
 */
public class RelationshipParser {

    /**
     * Parses a list of relationship maps into a list of RelationshipDefinition objects.
     *
     * @param relationshipMaps The relationship maps
     * @return The list of RelationshipDefinition objects
     */
    public List<RelationshipDefinition> parse(List<Map<String, Object>> relationshipMaps) {
        List<RelationshipDefinition> relationships = new ArrayList<>();

        for (Map<String, Object> relationshipMap : relationshipMaps) {
            relationships.add(parseRelationship(relationshipMap));
        }

        return relationships;
    }

    /**
     * Parses a relationship map into a RelationshipDefinition.
     *
     * @param relationshipMap The relationship map
     * @return The RelationshipDefinition
     */
    @SuppressWarnings("unchecked")
    private RelationshipDefinition parseRelationship(Map<String, Object> relationshipMap) {
        RelationshipDefinition.Builder builder = RelationshipDefinition.builder()
                .withFromEntity((String) relationshipMap.get("from"))
                .withToEntity((String) relationshipMap.get("to"));

        // Parse relationship type
        String typeStr = (String) relationshipMap.get("type");
        if (typeStr != null) {
            RelationshipDefinition.RelationType type = RelationshipDefinition.RelationType.valueOf(typeStr);
            builder.withType(type);
        }

        // Parse optional properties
        if (relationshipMap.containsKey("mappedBy")) {
            builder.withMappedBy((String) relationshipMap.get("mappedBy"));
        }

        if (relationshipMap.containsKey("joinColumn")) {
            builder.withJoinColumn((String) relationshipMap.get("joinColumn"));
        }

        if (relationshipMap.containsKey("joinTable")) {
            builder.withJoinTable((String) relationshipMap.get("joinTable"));
        }

        if (relationshipMap.containsKey("nullable")) {
            builder.withNullable((Boolean) relationshipMap.get("nullable"));
        }

        if (relationshipMap.containsKey("fetchEager")) {
            builder.withFetchEager((Boolean) relationshipMap.get("fetchEager"));
        }

        if (relationshipMap.containsKey("targetPackage")) {
            builder.withTargetPackage((String) relationshipMap.get("targetPackage"));
        }

        return builder.build();
    }
}