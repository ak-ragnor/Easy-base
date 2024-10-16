package com.easy.base.repository;


import com.easy.base.entity.MediaFile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaFileRepository extends MongoRepository<MediaFile,String> {
    public MediaFile findByParentIdAndFileName(long parentId, String fileName);
}
