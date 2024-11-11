package com.easy.base.controller.dto;

import com.easy.base.model.ChoiceList;
import com.easy.base.model.FlexiTable;
import org.bson.types.ObjectId;

import java.util.List;

public record ChoiceListDTO(
        String id,
        String name,
        List<String> options
) {
    public ChoiceListDTO(ChoiceList a) {
        this(
                a.getId().toString(),
                a.getName(),
                a.getOptions()
        );
    }

    public ChoiceList toChoiceList(String workspaceId) {
        return ChoiceList.builder()
                .id(new ObjectId(id))
                .workspaceId(new ObjectId(workspaceId))
                .name(name)
                .options(options)
                .build();
    }

    public ChoiceList toNewChoiceList(String workspaceId) {
        return ChoiceList.builder()
                .workspaceId(new ObjectId(workspaceId))
                .name(name)
                .options(options)
                .build();
    }

//    public ChoiceList toChoiceList(String workspaceId, boolean generateNewId) {
//        ObjectId objectId = generateNewId ? null : new ObjectId(id);
//        return ChoiceList.builder()
//                .id(objectId)
//                .workspaceId(new ObjectId(workspaceId))
//                .name(name)
//                .options(options)
//                .build();
//    }


}
