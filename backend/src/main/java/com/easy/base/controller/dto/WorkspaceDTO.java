package com.easy.base.controller.dto;

import com.easy.base.model.Workspace;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class WorkspaceDTO extends RepresentationModel<WorkspaceDTO> {
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private List<String> collaboratorIds;
    private Date createdDate;
    private Date modifiedDate;
    private List<FlexiTableDTO> flexiTables;

    public WorkspaceDTO(Workspace workspace) {
        this.id = workspace.getId() != null ? workspace.getId().toString() : null;
        this.name = workspace.getName();
        this.description = workspace.getDescription();
        this.ownerId = workspace.getOwnerId() != null ? workspace.getOwnerId().toString() : null;
        this.collaboratorIds = workspace.getCollaboratorId() != null
                ? workspace.getCollaboratorId().stream()
                .map(ObjectId::toString)
                .toList()
                : null;
        this.createdDate = workspace.getId() != null ?  workspace.getCreatedDate() : new Date();
        this.modifiedDate = workspace.getId() != null ?  workspace.getModifiedDate() : new Date();
        this.flexiTables = workspace.getFlexiTables() != null
                ? workspace.getFlexiTables().stream().map(FlexiTableDTO::new).toList()
                : List.of();
    }

    public Workspace toWorkspace() {
        Workspace workspace = new Workspace();
        if (id != null) {
            workspace.setId(new ObjectId(id));
        }
        workspace.setName(name);
        workspace.setDescription(description);
        if (ownerId != null) {
            workspace.setOwnerId(new ObjectId(ownerId));
        }
        if (collaboratorIds != null) {
            workspace.setCollaboratorId(collaboratorIds.stream()
                    .map(ObjectId::new)
                    .toList());
        }
        workspace.setCreatedDate(createdDate);
        workspace.setModifiedDate(modifiedDate);

        if (flexiTables != null) {
            workspace.setFlexiTables(flexiTables.stream()
                    .map(dto -> dto.toFlexiTable(id)) // Using workspace id for conversion
                    .toList());
        }

        return workspace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WorkspaceDTO that = (WorkspaceDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(ownerId, that.ownerId) &&
                Objects.equals(collaboratorIds, that.collaboratorIds) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(modifiedDate, that.modifiedDate) &&
                Objects.equals(flexiTables, that.flexiTables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, description, ownerId,
                collaboratorIds, createdDate, modifiedDate, flexiTables);
    }
}
