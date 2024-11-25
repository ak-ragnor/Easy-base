package com.easy.base.repository;



import com.easy.base.model.MediaFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MediaFileRepository extends MongoRepository<MediaFile,String> {
    public MediaFile findByParentIdAndFileName(String parentId, String fileName);
    public List<MediaFile> findByParentId(String parentId);
    public Page<MediaFile> findByParentId(String parentId, Pageable pageable);
}
