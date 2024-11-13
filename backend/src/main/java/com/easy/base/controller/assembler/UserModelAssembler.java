package com.easy.base.controller.assembler;

import com.easy.base.controller.UserController;
import com.easy.base.controller.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@org.springframework.hateoas.server.core.Relation(collectionRelation = "users")
@RequiredArgsConstructor
@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserDTO, EntityModel<UserDTO>> {

    @Override
    public EntityModel<UserDTO> toModel(UserDTO userDTO) {
        return EntityModel.of(userDTO,
                linkTo(methodOn(UserController.class).getUserById(userDTO.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(UserController.class).updateUser(userDTO.getId(), userDTO)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(userDTO.getId())).withRel("delete")
        );
    }

    @Override
    public CollectionModel<EntityModel<UserDTO>> toCollectionModel(Iterable<? extends UserDTO> entities) {
        CollectionModel<EntityModel<UserDTO>> userModels = RepresentationModelAssembler.super.toCollectionModel(entities);
        userModels.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        return userModels;
    }
}
