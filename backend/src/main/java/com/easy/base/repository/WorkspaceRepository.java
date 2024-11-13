package com.easy.base.repository;

import com.easy.base.model.Workspace;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceRepository extends MongoRepository<Workspace, String> {

    Page<Workspace> findByOwnerId(ObjectId ownerId, Pageable pageable);
}
