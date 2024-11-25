package com.easy.base.repository;


import com.easy.base.model.MediaFolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MediaFolderRepository extends MongoRepository<MediaFolder,String> {
    public List<MediaFolder> findByParentId(String parentId);
    public Page<MediaFolder> findByParentId(String parentId, Pageable pageable);
    public MediaFolder findByParentIdAndFolderName(String parentId, String name);
}
