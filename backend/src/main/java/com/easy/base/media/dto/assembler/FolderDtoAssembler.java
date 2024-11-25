package com.easy.base.media.dto.assembler;

import com.easy.base.media.controller.FolderController;
import com.easy.base.media.dto.FolderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@org.springframework.hateoas.server.core.Relation(collectionRelation = "mediaFolders")
@RequiredArgsConstructor
@Component
public class FolderDtoAssembler implements RepresentationModelAssembler<FolderDto, EntityModel<FolderDto>> {
    @Override
    public EntityModel<FolderDto> toModel(FolderDto entity) {

        EntityModel<FolderDto> entityModel = EntityModel.of(entity);
        try {
            entityModel.add(
                    linkTo(methodOn(FolderController.class)
                            .getAllFolders( entity.getFolderId(), Pageable.unpaged()))
                            .withRel("child folders"),
                    linkTo(methodOn(FolderController.class)
                            .createFolder(entity))
                            .withRel("create"),
                    linkTo(methodOn(FolderController.class)
                            .deleteFolder(entity.getFolderId()))
                            .withRel("delete"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entityModel;
    }
}
