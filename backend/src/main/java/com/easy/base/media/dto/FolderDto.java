package com.easy.base.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderDto extends RepresentationModel<FolderDto> {
    private String folderId;
    private String folderName;
    private String parentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Compare RepresentationModel links

        FolderDto that = (FolderDto) o;
        return Objects.equals(folderId, that.folderId) &&
                Objects.equals(folderName, that.folderName) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), folderId, folderName);
    }
}
