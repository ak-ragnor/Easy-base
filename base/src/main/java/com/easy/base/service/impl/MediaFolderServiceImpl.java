package com.easy.base.service.impl;

import com.easy.base.entity.MediaFolder;
import com.easy.base.exception.media.DuplicateFileNameExeption;
import com.easy.base.exception.media.InvalidFileIdException;
import com.easy.base.repository.MediaFileRepository;
import com.easy.base.repository.MediaFolderRepository;
import com.easy.base.service.MediaFolderService;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;

@Service
public class MediaFolderServiceImpl implements MediaFolderService {
    private final MediaFolderRepository mediaFolderRepository;
    private final MediaFileRepository mediaFileRepository;

    public MediaFolderServiceImpl(MediaFolderRepository mediaFolderRepository, MediaFileRepository mediaFileRepository) {
        this.mediaFolderRepository = mediaFolderRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    public String findPath(String folderId) {
        return mediaFolderRepository.findById(folderId).orElseThrow(()->new InvalidFileIdException("folder",folderId)).getFolderPath();
    }

    @Override
    public String createFolderPath(String parentId) throws InvalidFileIdException {
        return findPath(parentId)+parentId+"/";
    }

    @Override
    public void deleteFolders(String folderId) {
        mediaFolderRepository.delete( mediaFolderRepository.findById(folderId).orElseThrow(()->new InvalidFileIdException("folder",folderId)));
        mediaFolderRepository.deleteAll(findByparentId(folderId));
        mediaFileRepository.deleteAll(mediaFileRepository.findByParentId(folderId));
    }

    private MediaFolder addMediaFolder(String folderName, String parentId, String folderPath){
        if(findByParentIdAndFolderName(parentId,folderName)!=null) throw new DuplicateFileNameExeption(folderName,parentId);
        return mediaFolderRepository.save(MediaFolder.builder().folderName(folderName).parentId(parentId).folderPath(folderPath).build());
    }

    @Override
    public MediaFolder addFolder(String name, String parentId) throws InvalidFileIdException, DuplicateFileNameExeption{
        return this.addMediaFolder(name,parentId,createFolderPath(parentId));
    }

    @Override
    public MediaFolder addFolder(String name)throws  DuplicateFileNameExeption{
        return this.addMediaFolder(name,"","/");
    }

    @Override
    public List<MediaFolder> findByparentId(String patrentId) {
        return mediaFolderRepository.findByParentId(patrentId);
    }

    @Override
    public MediaFolder findByParentIdAndFolderName(String parentId, String folderName) {
        return mediaFolderRepository.findByParentIdAndFolderName(parentId,folderName);
    }
}
