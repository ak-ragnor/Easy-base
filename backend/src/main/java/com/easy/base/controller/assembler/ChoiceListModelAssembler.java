package com.easy.base.controller.assembler;

import com.easy.base.controller.ChoiceListController;
import com.easy.base.controller.dto.ChoiceListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@org.springframework.hateoas.server.core.Relation(collectionRelation = "choice_list")
@RequiredArgsConstructor
@Component
public class ChoiceListModelAssembler implements RepresentationModelAssembler<ChoiceListDTO, EntityModel<ChoiceListDTO>> {

    @Override
    public EntityModel<ChoiceListDTO> toModel(ChoiceListDTO choiceList) {
        EntityModel<ChoiceListDTO> entityModel = EntityModel.of(choiceList);

        entityModel.add(
                linkTo(methodOn(ChoiceListController.class)
                        .getChoiceListById(choiceList.getWorkspaceId(), choiceList.getId()))
                        .withSelfRel(),
                linkTo(methodOn(ChoiceListController.class)
                        .getAllChoiceLists(choiceList.getWorkspaceId(), Pageable.unpaged()))
                        .withRel("choices"),
                linkTo(methodOn(ChoiceListController.class)
                        .updateChoiceList(choiceList.getWorkspaceId(), choiceList.getId(), choiceList))
                        .withRel("update"),
                linkTo(methodOn(ChoiceListController.class)
                        .deleteChoiceList(choiceList.getWorkspaceId(), choiceList.getId()))
                        .withRel("delete")
        );

        return entityModel;
    }
}
