package com.easybase.generator.parser;

import com.easybase.generator.model.EntityDefinition;
import com.easybase.generator.model.DtoLevel;
import com.easybase.generator.model.ListenerDefinition;
import com.easybase.generator.model.FinderDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for the entity section of a YAML configuration.
 */
public class EntityParser {

    /**
     * Parses an entity map into an EntityDefinition.
     *
     * @param entityMap The entity map
     * @return The EntityDefinition
     */
    @SuppressWarnings("unchecked")
    public EntityDefinition parse(Map<String, Object> entityMap) {
        EntityDefinition.Builder builder = EntityDefinition.builder()
                .withName((String) entityMap.get("name"))
                .withTable((String) entityMap.get("table"))
                .withPackage((String) entityMap.get("package"));

        // Parse options
        if (entityMap.containsKey("options")) {
            Map<String, Object> options = (Map<String, Object>) entityMap.get("options");
            builder.withOptions(options);
        }

        // Parse audit config
        if (entityMap.containsKey("audit")) {
            Map<String, Object> auditMap = (Map<String, Object>) entityMap.get("audit");
            EntityDefinition.AuditConfig auditConfig = new EntityDefinition.AuditConfig();

            if (auditMap.containsKey("enabled")) {
                auditConfig.setEnabled((Boolean) auditMap.get("enabled"));
            }

            if (auditMap.containsKey("fields")) {
                auditConfig.setFields((List<String>) auditMap.get("fields"));
            }

            builder.withAuditConfig(auditConfig);
        }

        // Parse DTO levels
        if (entityMap.containsKey("dtoLevels")) {
            List<Map<String, Object>> dtoLevelMaps = (List<Map<String, Object>>) entityMap.get("dtoLevels");
            List<DtoLevel> dtoLevels = new ArrayList<>();

            for (Map<String, Object> dtoLevelMap : dtoLevelMaps) {
                DtoLevel dtoLevel = new DtoLevel();
                dtoLevel.setName((String) dtoLevelMap.get("name"));
                dtoLevel.setFields((List<String>) dtoLevelMap.get("fields"));
                dtoLevels.add(dtoLevel);
            }

            builder.withDtoLevels(dtoLevels);
        }

        // Parse listeners
        if (entityMap.containsKey("listeners")) {
            List<Map<String, Object>> listenerMaps = (List<Map<String, Object>>) entityMap.get("listeners");
            List<ListenerDefinition> listeners = new ArrayList<>();

            for (Map<String, Object> listenerMap : listenerMaps) {
                ListenerDefinition listener = new ListenerDefinition();
                listener.setType((String) listenerMap.get("type"));
                listener.setMethod((String) listenerMap.get("method"));
                listeners.add(listener);
            }

            builder.withListeners(listeners);
        }

        // Parse finders
        if (entityMap.containsKey("finders")) {
            List<Map<String, Object>> finderMaps = (List<Map<String, Object>>) entityMap.get("finders");
            List<FinderDefinition> finders = new ArrayList<>();

            for (Map<String, Object> finderMap : finderMaps) {
                FinderDefinition finder = new FinderDefinition();
                finder.setName((String) finderMap.get("name"));
                finder.setReturnType((String) finderMap.get("returnType"));

                if (finderMap.containsKey("query")) {
                    finder.setQuery((String) finderMap.get("query"));
                }

                if (finderMap.containsKey("parameters")) {
                    List<Map<String, Object>> parameterMaps = (List<Map<String, Object>>) finderMap.get("parameters");
                    List<FinderDefinition.Parameter> parameters = new ArrayList<>();

                    for (Map<String, Object> parameterMap : parameterMaps) {
                        FinderDefinition.Parameter parameter = new FinderDefinition.Parameter();
                        parameter.setName((String) parameterMap.get("name"));
                        parameter.setType((String) parameterMap.get("type"));
                        parameters.add(parameter);
                    }

                    finder.setParameters(parameters);
                }

                finders.add(finder);
            }

            builder.withFinders(finders);
        }

        return builder.build();
    }
}