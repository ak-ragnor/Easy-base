package com.easy.base.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDetailsDto extends RepresentationModel<FileDetailsDto> {
    private String fileId;
    private String fileName;
    private String url;
    private Date createDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Compare RepresentationModel links

        FileDetailsDto that = (FileDetailsDto) o;
        return Objects.equals(fileId, that.fileId) &&
                Objects.equals(fileName, that.fileName) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileId, fileName);
    }
}
