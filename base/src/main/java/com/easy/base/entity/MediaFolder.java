package com.easy.base.entity;

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
@Document("mediaFolders")
public class MediaFolder extends Auditing{
    @Id
    private String folderId;
    private String folderName;
    private String folderPath;
    private String parentId;

}
