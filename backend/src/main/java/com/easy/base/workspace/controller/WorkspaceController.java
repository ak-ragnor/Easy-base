package com.easy.base.workspace.controller;

import com.easy.base.workspace.controller.assembler.WorkspaceModelAssembler;
import com.easy.base.workspace.controller.dto.WorkspaceDTO;
import com.easy.base.model.Workspace;
import com.easy.base.service.WorkspaceService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final PagedResourcesAssembler<WorkspaceDTO> pagedResourcesAssembler;
    private final WorkspaceModelAssembler workspaceModelAssembler;

    public WorkspaceController(WorkspaceService workspaceService,
                               PagedResourcesAssembler<WorkspaceDTO> pagedResourcesAssembler,
                               WorkspaceModelAssembler workspaceModelAssembler) {
        this.workspaceService = workspaceService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.workspaceModelAssembler = workspaceModelAssembler;
    }

    @PostMapping
    public ResponseEntity<EntityModel<WorkspaceDTO>> createWorkspace(@Valid @RequestBody WorkspaceDTO workspaceDTO) {
        WorkspaceDTO created = new WorkspaceDTO(workspaceService.createWorkspace(workspaceDTO.toWorkspace()));

        EntityModel<WorkspaceDTO> entityModel = workspaceModelAssembler.toModel(created);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<WorkspaceDTO>>> getWorkspacesByUserId(
            @PathVariable String userId,
            @PageableDefault(size = 20, sort = "createdDate") Pageable pageable) {

        Page<WorkspaceDTO> workspacesPage = workspaceService.getWorkspacesByUserId(userId, pageable)
                .map(WorkspaceDTO::new);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(
                workspacesPage, workspaceModelAssembler));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<WorkspaceDTO>> getWorkspaceById(@PathVariable String id) {

        WorkspaceDTO workspace =  new WorkspaceDTO(workspaceService.getWorkspaceById(id));
        return ResponseEntity.ok(workspaceModelAssembler.toModel(workspace));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<WorkspaceDTO>> updateWorkspace(
            @PathVariable String id,
            @Valid @RequestBody WorkspaceDTO workspaceDTO) {

        Workspace workspace = workspaceDTO.toWorkspace();
        workspace.setId(new ObjectId(id));

        WorkspaceDTO updatedWorkspace = new WorkspaceDTO(workspaceService.updateWorkspace(workspace));


        return ResponseEntity.ok(workspaceModelAssembler.toModel(updatedWorkspace));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable String id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/collaborators")
    public ResponseEntity<EntityModel<WorkspaceDTO>> updateCollaborators(
            @PathVariable String id,
            @Valid @RequestBody List<String> collaboratorIds) {

        WorkspaceDTO workspace = new WorkspaceDTO(workspaceService.updateCollaborators(id,
                collaboratorIds.stream().map(ObjectId::new).toList()));

        return ResponseEntity.ok(workspaceModelAssembler.toModel(workspace));
    }
}
