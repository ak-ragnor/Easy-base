package com.easy.base.controller.dto;

import com.easy.base.model.User;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDTO extends RepresentationModel<UserDTO> {
    private String id;
    private String username;
    private String email;
    private boolean active;
    private Date createdDate;
    private Date modifiedDate;
    private List<WorkspaceDTO> workspaces;

    public UserDTO(User user) {
        this.id = user.getId() != null ? user.getId().toString() : null;
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.active = user.isActive();
        this.createdDate = user.getCreatedDate();
        this.modifiedDate = user.getModifiedDate();
        this.workspaces = user.getWorkspaces() != null
                ? user.getWorkspaces().stream().map(WorkspaceDTO::new).toList()
                : null;
    }

    public User toUser() {
        User user = new User();
        if (id != null) {
            user.setId(new ObjectId(id));
        }
        user.setUsername(username);
        user.setEmail(email);
        user.setActive(active);
        user.setCreatedDate(createdDate);
        user.setModifiedDate(modifiedDate);
        if (workspaces != null) {
            user.setWorkspaces(workspaces.stream()
                    .map(WorkspaceDTO::toWorkspace)
                    .collect(Collectors.toList()));
        }
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserDTO userDTO = (UserDTO) o;
        return active == userDTO.active &&
                Objects.equals(id, userDTO.id) &&
                Objects.equals(username, userDTO.username) &&
                Objects.equals(email, userDTO.email) &&
                Objects.equals(createdDate, userDTO.createdDate) &&
                Objects.equals(modifiedDate, userDTO.modifiedDate) &&
                Objects.equals(workspaces, userDTO.workspaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, username, email, active,
                createdDate, modifiedDate, workspaces);
    }
}
