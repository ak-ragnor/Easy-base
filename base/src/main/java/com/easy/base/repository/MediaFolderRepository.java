package com.easy.base.repository;

import com.easy.base.entity.MediaFolder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaFolderRepository extends MongoRepository<MediaFolder,Long> {
}
