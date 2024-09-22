package com.easy.base.media.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("mediafolders")
public class MediaFolder {
    @Id
    private long folderId;
    private String folderName;
    private String folderPath;
    private long parentId;
    private Date createdDate;
    private Date modifiedDate;
}
