package com.easy.base.workspace.controller.assembler;

import com.easy.base.workspace.controller.WorkspaceController;
import com.easy.base.workspace.controller.dto.WorkspaceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@org.springframework.hateoas.server.core.Relation(collectionRelation = "workspaces")
@RequiredArgsConstructor
@Component
public class WorkspaceModelAssembler implements RepresentationModelAssembler<WorkspaceDTO, EntityModel<WorkspaceDTO>> {

    @Override
    public EntityModel<WorkspaceDTO> toModel(WorkspaceDTO workspace) {
        EntityModel<WorkspaceDTO> entityModel = EntityModel.of(workspace);

        entityModel.add(
                linkTo(methodOn(WorkspaceController.class)
                        .getWorkspaceById(workspace.getId()))
                        .withSelfRel(),
                linkTo(methodOn(WorkspaceController.class)
                        .updateWorkspace(workspace.getId(), workspace))
                        .withRel("update"),
                linkTo(methodOn(WorkspaceController.class)
                        .deleteWorkspace(workspace.getId()))
                        .withRel("delete")
        );

        return entityModel;
    }
}
