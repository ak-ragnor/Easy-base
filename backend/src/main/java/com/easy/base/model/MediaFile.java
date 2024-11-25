package com.easy.base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("mediaFiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFile extends Auditing{

    @Id
    private String fileId;
    private String fileName;
    private String mimeType;
    private String filePath;
    private String parentId;
}
