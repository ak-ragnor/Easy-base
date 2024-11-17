package com.easy.base.repository;

import com.easy.base.entity.MediaFolder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MediaFolderRepository extends MongoRepository<MediaFolder,String> {
    public List<MediaFolder> findByParentId(String parentId);
    public MediaFolder findByParentIdAndFolderName(String parentId, String name);
}
