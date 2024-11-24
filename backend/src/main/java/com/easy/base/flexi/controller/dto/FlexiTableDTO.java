package com.easy.base.flexi.controller.dto;

import com.easy.base.model.FlexiTable;
import org.bson.types.ObjectId;

import java.util.List;

public record FlexiTableDTO(
        String id,
        String name,
        String description,
        List<FlexiTableFieldDTO> fields) {

    public FlexiTableDTO(FlexiTable a) {
        this(
                a.getId().toString(),
                a.getName(),
                a.getDescription(),
                a.getFields().stream().map(FlexiTableFieldDTO::new).toList()
        );
    }

    public FlexiTable toFlexiTable(String workspaceId) {
        return FlexiTable.builder()
                .id(new ObjectId(id))
                .name(name)
                .workspaceId(new ObjectId(workspaceId))
                .description(description)
                .flexiName(workspaceId+"_"+name)
                .fields(fields.stream().map(FlexiTableFieldDTO::toFlexiTableField).toList())
                .build();
    }

    public FlexiTable toNewFlexiTable(String workspaceId) {
        return FlexiTable.builder()
                .name(name)
                .workspaceId(new ObjectId(workspaceId))
                .description(description)
                .flexiName(workspaceId+"_"+name)
                .fields(fields.stream().map(FlexiTableFieldDTO::toFlexiTableField).toList())
                .build();
    }


}
