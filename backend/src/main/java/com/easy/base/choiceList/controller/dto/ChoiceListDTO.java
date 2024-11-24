package com.easy.base.choiceList.controller.dto;

import com.easy.base.model.ChoiceList;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ChoiceListDTO extends RepresentationModel<ChoiceListDTO> {

    private String id;
    private String name;
    private List<String> options;
    private String workspaceId;

    public ChoiceListDTO(ChoiceList choiceList) {
        this.id = choiceList.getId() != null ? choiceList.getId().toString() : null;
        this.name = choiceList.getName();
        this.options = choiceList.getOptions();
        this.workspaceId = String.valueOf(choiceList.getWorkspaceId());
    }

    public ChoiceList toChoiceList(String workspaceId) {
        return ChoiceList.builder()
                .id(id != null ? new ObjectId(id) : null)
                .workspaceId(new ObjectId(workspaceId))
                .name(name)
                .options(options)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Compare RepresentationModel links

        ChoiceListDTO that = (ChoiceListDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, options);
    }
}
