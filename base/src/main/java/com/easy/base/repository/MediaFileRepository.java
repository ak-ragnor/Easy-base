package com.easy.base.repository;


import com.easy.base.entity.MediaFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MediaFileRepository extends MongoRepository<MediaFile,String> {
    public MediaFile findByParentIdAndFileName(String parentId, String fileName);
    public List<MediaFile> findByParentId(String parentId);
}
