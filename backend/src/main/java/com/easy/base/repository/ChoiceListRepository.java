package com.easy.base.repository;

import com.easy.base.model.ChoiceList;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceListRepository extends MongoRepository<ChoiceList, String> {
    boolean existsByNameAndWorkspaceId(String name, ObjectId workspaceId);
}
