package com.easy.base.media.dto.assembler;

import com.easy.base.media.dto.FileDetailsDto;
import com.easy.base.media.controller.FileController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@org.springframework.hateoas.server.core.Relation(collectionRelation = "mediaFiles")
@RequiredArgsConstructor
@Component
public class FileDtoAssembler implements RepresentationModelAssembler<FileDetailsDto, EntityModel<FileDetailsDto>> {
    @Override
    public EntityModel<FileDetailsDto> toModel(FileDetailsDto entity) {
        EntityModel<FileDetailsDto> entityModel = EntityModel.of(entity);
        try {
            entityModel.add(
                    linkTo(methodOn(FileController.class)
                            .getFileDetails( entity.getFileId()))
                            .withRel("read"),
                    linkTo(methodOn(FileController.class)
                            .deleteFile(entity.getFileId()))
                            .withRel("delete"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entityModel;
    }
}
