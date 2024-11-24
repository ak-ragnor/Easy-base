package com.easy.base.superUser.controller;

import com.easy.base.superUser.controller.assembler.UserModelAssembler;
import com.easy.base.superUser.controller.dto.UserDTO;
import com.easy.base.model.User;
import com.easy.base.service.UserService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    public UserController(UserService userService, UserModelAssembler userModelAssembler) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserDTO>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        User user = userService.createUser(userDTO.toUser());
        EntityModel<UserDTO> entityModel = userModelAssembler.toModel(new UserDTO(user));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(
                userModelAssembler.toCollectionModel(
                        userService.getAllUsers().stream()
                                .map(UserDTO::new)
                                .toList()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(userModelAssembler.toModel(new UserDTO(user))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        User updatedUser = userService.updateUser(userDTO.toUser());
        EntityModel<UserDTO> entityModel = userModelAssembler.toModel(new UserDTO(updatedUser));
        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
