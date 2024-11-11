package com.easy.base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "workspaces")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workspace {

    @Id
    private ObjectId id;
    private String name;
    private String description;
    private ObjectId ownerId;
    private List<ObjectId> collaboratorId;

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;

    @DBRef
    private List<FlexiTable> flexiTables;

}
