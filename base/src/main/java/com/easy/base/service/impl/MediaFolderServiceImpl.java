package com.easy.base.service.impl;

import com.easy.base.entity.MediaFolder;
import com.easy.base.repository.MediaFileRepository;
import com.easy.base.repository.MediaFolderRepository;
import com.easy.base.service.MediaFolderService;

import java.util.List;

public class MediaFolderServiceImpl implements MediaFolderService {
    private final MediaFolderRepository mediaFolderRepository;
    private final MediaFileRepository mediaFileRepository;

    public MediaFolderServiceImpl(MediaFolderRepository mediaFolderRepository, MediaFileRepository mediaFileRepository) {
        this.mediaFolderRepository = mediaFolderRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public String findPath(String folderId) {
        return mediaFolderRepository.findById(folderId).orElse(null).getFolderPath();
    }

    @Override
    public String createFolderPath(String parentId) {
        return findPath(parentId)+parentId+"/";
    }

    @Override
    public void deleteFolders(String folderId) {
        mediaFolderRepository.deleteAll(findByparentId(folderId));
        mediaFolderRepository.deleteById(folderId);
        mediaFileRepository.deleteAll(mediaFileRepository.findByParentId(folderId));
    }

    private MediaFolder addMediaFolder(String folderName, String parentId, String folderPath){
        return mediaFolderRepository.save(MediaFolder.builder().folderName(folderName).parentId(parentId).folderPath(folderPath).build());
    }

    @Override
    public MediaFolder addFolder(String name, String parentId) {
        return this.addMediaFolder(name,parentId,createFolderPath(parentId));
    }

    @Override
    public MediaFolder addFolder(String name) {
        return this.addMediaFolder(name,"","/");
    }

    @Override
    public List<MediaFolder> findByparentId(String patrentId) {
        return mediaFolderRepository.findByParentId(patrentId);
    }
}
