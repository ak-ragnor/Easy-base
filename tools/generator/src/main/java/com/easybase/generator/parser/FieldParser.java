package com.easybase.generator.parser;

import com.easybase.generator.model.FieldDefinition;
import com.easybase.generator.model.SearchMapping;
import com.easybase.generator.model.ValidationRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parser for the fields section of a YAML configuration.
 */
public class FieldParser {

    /**
     * Parses a list of field maps into a list of FieldDefinition objects.
     *
     * @param fieldMaps The field maps
     * @return The list of FieldDefinition objects
     */
    public List<FieldDefinition> parse(List<Map<String, Object>> fieldMaps) {
        List<FieldDefinition> fields = new ArrayList<>();

        for (Map<String, Object> fieldMap : fieldMaps) {
            fields.add(parseField(fieldMap));
        }

        return fields;
    }

    /**
     * Parses a field map into a FieldDefinition.
     *
     * @param fieldMap The field map
     * @return The FieldDefinition
     */
    @SuppressWarnings("unchecked")
    private FieldDefinition parseField(Map<String, Object> fieldMap) {
        FieldDefinition.Builder builder = FieldDefinition.builder()
                .withName((String) fieldMap.get("name"))
                .withType((String) fieldMap.get("type"));

        // Parse boolean properties
        if (fieldMap.containsKey("primaryKey")) {
            builder.withPrimaryKey((Boolean) fieldMap.get("primaryKey"));
        }

        if (fieldMap.containsKey("nullable")) {
            builder.withNullable((Boolean) fieldMap.get("nullable"));
        }

        if (fieldMap.containsKey("unique")) {
            builder.withUnique((Boolean) fieldMap.get("unique"));
        }

        if (fieldMap.containsKey("generated")) {
            builder.withGenerated((Boolean) fieldMap.get("generated"));
        }

        // Parse numeric and string properties
        if (fieldMap.containsKey("length")) {
            builder.withLength((Integer) fieldMap.get("length"));
        }

        if (fieldMap.containsKey("defaultValue")) {
            builder.withDefaultValue(fieldMap.get("defaultValue"));
        }

        if (fieldMap.containsKey("description")) {
            builder.withDescription((String) fieldMap.get("description"));
        }

        // Parse enum properties
        if (fieldMap.containsKey("enumClass")) {
            builder.withEnumClass((String) fieldMap.get("enumClass"));
        }

        if (fieldMap.containsKey("values")) {
            builder.withEnumValues((List<String>) fieldMap.get("values"));
        }

        // Parse validations
        if (fieldMap.containsKey("validation")) {
            List<Map<String, Object>> validationMaps = (List<Map<String, Object>>) fieldMap.get("validation");
            List<ValidationRule> validations = new ArrayList<>();

            for (Map<String, Object> validationMap : validationMaps) {
                ValidationRule.Builder validationBuilder = ValidationRule.builder()
                        .withType((String) validationMap.get("type"));

                if (validationMap.containsKey("message")) {
                    validationBuilder.withMessage((String) validationMap.get("message"));
                }

                // Add all other properties as parameters
                for (Map.Entry<String, Object> entry : validationMap.entrySet()) {
                    if (!entry.getKey().equals("type") && !entry.getKey().equals("message")) {
                        validationBuilder.withParameter(entry.getKey(), entry.getValue());
                    }
                }

                validations.add(validationBuilder.build());
            }

            builder.withValidations(validations);
        }

        // Parse search mapping
        if (fieldMap.containsKey("search")) {
            Map<String, Object> searchMap = (Map<String, Object>) fieldMap.get("search");
            SearchMapping.Builder searchBuilder = SearchMapping.builder()
                    .withType((String) searchMap.get("type"));

            if (searchMap.containsKey("analyzer")) {
                searchBuilder.withAnalyzer((String) searchMap.get("analyzer"));
            }

            if (searchMap.containsKey("fields")) {
                searchBuilder.withFields((Map<String, Object>) searchMap.get("fields"));
            }

            if (searchMap.containsKey("properties")) {
                searchBuilder.withProperties((Map<String, Object>) searchMap.get("properties"));
            }

            builder.withSearchMapping(searchBuilder.build());
        }

        return builder.build();
    }
}