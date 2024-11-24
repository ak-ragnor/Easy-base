package com.easy.base.choiceList.controller;

import com.easy.base.choiceList.controller.assembler.ChoiceListModelAssembler;
import com.easy.base.choiceList.controller.dto.ChoiceListDTO;
import com.easy.base.service.ChoiceListService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ChoiceListController {

    private final ChoiceListService choiceListService;
    private final PagedResourcesAssembler<ChoiceListDTO> pagedResourcesAssembler;
    private final ChoiceListModelAssembler choiceListModelAssembler;

    public ChoiceListController(ChoiceListService choiceListService,
                                PagedResourcesAssembler<ChoiceListDTO> pagedResourcesAssembler,
                                ChoiceListModelAssembler choiceListModelAssembler) {
        this.choiceListService = choiceListService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.choiceListModelAssembler = choiceListModelAssembler;
    }

    @PostMapping(value = "/{workspaceId}/choices", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ChoiceListDTO>> createChoiceList(
            @PathVariable String workspaceId,
            @RequestBody ChoiceListDTO choiceList) {

        ChoiceListDTO created = new ChoiceListDTO(
                choiceListService.createChoiceList(choiceList.toChoiceList(workspaceId))
        );
        EntityModel<ChoiceListDTO> entityModel = choiceListModelAssembler.toModel(created);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping(value = "/{workspaceId}/choices/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ChoiceListDTO>> getChoiceListById(
            @PathVariable String workspaceId,
            @PathVariable String id) {

        ChoiceListDTO choiceList = new ChoiceListDTO(choiceListService.getChoiceListById(id));
        return ResponseEntity.ok(choiceListModelAssembler.toModel(choiceList));
    }

    @GetMapping(value = "/{workspaceId}/choices", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<ChoiceListDTO>>> getAllChoiceLists(
            @PathVariable String workspaceId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<ChoiceListDTO> choiceListsPage = choiceListService.getAllChoiceLists(pageable)
                .map(ChoiceListDTO::new);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(
                choiceListsPage, choiceListModelAssembler));
    }

    @PutMapping(value = "/{workspaceId}/choices/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<ChoiceListDTO>> updateChoiceList(
            @PathVariable String workspaceId,
            @PathVariable String id,
            @RequestBody ChoiceListDTO choiceList) {
        choiceList.setId(id);
        ChoiceListDTO updated = new ChoiceListDTO(
                choiceListService.updateChoiceList(choiceList.toChoiceList(workspaceId))
        );
        return ResponseEntity.ok(choiceListModelAssembler.toModel(updated));
    }

    @DeleteMapping("/{workspaceId}/choices/{id}")
    public ResponseEntity<Void> deleteChoiceList(
            @PathVariable String workspaceId,
            @PathVariable String id) {
        choiceListService.deleteChoiceList(id);
        return ResponseEntity.noContent().build();
    }

}
