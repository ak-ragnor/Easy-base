package com.easy.base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "flexi_tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlexiTable {

    @Id
    private ObjectId id;
    private String name;
    private ObjectId workspaceId;
    private String description;
    private String flexiName;
    private List<FlexiTableField> fields;

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;

    public String getCollectionName(){

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char currentChar : name.toCharArray()) {
            if (Character.isWhitespace(currentChar)) {
                capitalizeNext = true;
            } else {
                result.append(capitalizeNext ? Character.toUpperCase(currentChar) : Character.toLowerCase(currentChar));
                capitalizeNext = false;
            }
        }

        result.setCharAt(0, Character.toLowerCase(result.charAt(0)));

        return workspaceId + "_" + result.toString();
    }
}
