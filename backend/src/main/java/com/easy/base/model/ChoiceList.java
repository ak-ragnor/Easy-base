package com.easy.base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(collection = "choice_list")
@CompoundIndex(def = "{'name': 1, 'workspaceId': 1}", unique = true)
public class ChoiceList {

    @Id
    private ObjectId id;
    private List<String> options;
    private String name;
    private ObjectId workspaceId;


    public void validate() {
        if (options != null && options.size() != new HashSet<>(options).size()) {
            throw new IllegalArgumentException("Options must be unique.");
        }
    }
}
