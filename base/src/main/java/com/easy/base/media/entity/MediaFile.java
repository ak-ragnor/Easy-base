package com.easy.base.media.entity;

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
public class MediaFile {

    @Id
    private long fileId;
    private String fileName;
    private String mimeType;
    private String filePath;
    private long parentId;

    @CreatedDate
    private Date createdDate;
    
    @LastModifiedDate
    private Date modifiedDate;
}
