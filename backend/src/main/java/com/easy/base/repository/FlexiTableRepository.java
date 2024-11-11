package com.easy.base.repository;

import com.easy.base.model.FlexiTable;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlexiTableRepository extends MongoRepository<FlexiTable, String> {
    Optional<FlexiTable> findByNameAndWorkspaceId( String name, ObjectId workspaceId);

    boolean existsByNameAndWorkspaceId(String name, ObjectId workspaceId);
}