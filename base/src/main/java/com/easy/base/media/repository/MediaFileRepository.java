package com.easy.base.media.repository;


import com.easy.base.media.entity.MediaFile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaFileRepository extends MongoRepository<MediaFile,Long> {
    public MediaFile findByParentIdAndFileName(long parentId, String fileName);
}
