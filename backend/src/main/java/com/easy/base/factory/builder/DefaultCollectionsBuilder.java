package com.easy.base.factory.builder;

import com.easy.base.model.FlexiTable;
import com.easy.base.model.FlexiTableField;
import com.easy.base.model.Workspace;
import com.easy.base.model.enums.FieldTypes;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class DefaultCollectionsBuilder{

    private final Map<String, Function<Workspace, FlexiTable>> collectionCreators;

    public DefaultCollectionsBuilder() {
        collectionCreators = new ConcurrentHashMap<>();
        collectionCreators.put("Users", this :: _createUsersCollection);
    }

    public List<FlexiTable> createDefaultCollections(Workspace workspace) {
        List<FlexiTable> defaultCollections = new ArrayList<>();

        for (Map.Entry<String, Function<Workspace, FlexiTable>> entry : collectionCreators.entrySet()) {
            FlexiTable table = entry.getValue().apply(workspace);
            if (table != null) {
                defaultCollections.add(table);
            }
        }

        return defaultCollections;
    }

    private FlexiTable _createUsersCollection(Workspace workspace) {
        return FlexiTable.builder()
                .name("Users")
                .workspaceId(workspace.getId())
                .description("Default Users Table")
                .flexiName(workspace.getId() + "_Users")
                .fields(List.of(
                        new FlexiTableField("username", FieldTypes.TEXT, true, true, null),
                        new FlexiTableField("email", FieldTypes.TEXT, true, true, null),
                        new FlexiTableField("role", FieldTypes.TEXT, true, false, null),
                        new FlexiTableField("isActive", FieldTypes.BOOLEAN, true, true, null),
                        new FlexiTableField("createdDate", FieldTypes.DATE, true, false, null),
                        new FlexiTableField("modifiedDate", FieldTypes.DATE, true, false, null)
                ))
                .build();
    }
}
