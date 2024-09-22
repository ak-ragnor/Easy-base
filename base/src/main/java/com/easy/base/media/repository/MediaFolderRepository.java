package com.easy.base.media.repository;

import com.easy.base.media.entity.MediaFolder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaFolderRepository extends MongoRepository<MediaFolder,Long> {
}
