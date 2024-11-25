package com.easy.base.service.impl;


import com.easy.base.exception.media.DuplicateFileNameExeption;
import com.easy.base.exception.media.InvalidFileIdException;
import com.easy.base.model.MediaFile;
import com.easy.base.repository.MediaFileRepository;
import com.easy.base.repository.MediaFolderRepository;
import com.easy.base.service.MediaFileService;
import com.easy.base.media.util.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Service
public class MediaFileServiceImpl implements MediaFileService {
    private final MediaFileRepository mediaFileRepository;
    private final MediaFolderRepository mediaFolderRepository;

    public MediaFileServiceImpl(MediaFileRepository mediaFileRepository, MediaFolderRepository mediaFolderRepository) {
        this.mediaFileRepository = mediaFileRepository;
        this.mediaFolderRepository = mediaFolderRepository;
    }

    @Transactional
    private MediaFile addMediaFile(String fileName, String mimeType, String filePath, String parentId, InputStream inputStream) {
        if(mediaFileRepository.findByParentIdAndFileName(parentId, fileName) != null){
            throw new DuplicateFileNameExeption(fileName,parentId);
        }
        Store.saveFile(inputStream,filePath,fileName,mimeType);
        return mediaFileRepository.save(MediaFile.builder().filePath(filePath).fileName(fileName).mimeType(mimeType).parentId(parentId).build());
    }
    @Transactional
    @Override
    public MediaFile addFile(String fileName, String mimeType, String parentId, InputStream is) throws DuplicateFileNameExeption, InvalidFileIdException {
        return this.addMediaFile(fileName,mimeType,createPath(parentId),parentId,is);
    }

    @Transactional
    @Override
    public MediaFile addFile(String fileName, String mimeType, InputStream is) throws DuplicateFileNameExeption{
        return this.addMediaFile(fileName,mimeType,"/","",is);
    }

    @Transactional(readOnly = true)
    @Override
    public String findPath(String fileId) {
        return mediaFileRepository.findById(fileId).orElseThrow(()-> new InvalidFileIdException(fileId)).getFilePath();
    }

    @Transactional
    @Override
    public String createPath(String parentId) {
        return mediaFolderRepository.findById(parentId).orElseThrow(()-> new InvalidFileIdException(parentId)).getFolderPath()+parentId+"/";
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MediaFile> findByParentId(String parentId, Pageable pageable) {
        return mediaFileRepository.findByParentId(parentId,pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public MediaFile findById(String fileId) {
        return mediaFileRepository.findById(fileId).orElse(null);
    }

    @Transactional
    @Override
    public void delete(String id) {
        mediaFileRepository.delete( mediaFileRepository.findById(id).orElseThrow(()->new InvalidFileIdException("file",id)));
    }
}
