package com.easy.base.service.impl;

import com.easy.base.entity.MediaFile;
import com.easy.base.repository.MediaFileRepository;
import com.easy.base.service.MediaFileService;
import com.easy.base.media.util.Store;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MediaFileServiceImpl implements MediaFileService {
    private final MediaFileRepository mediaFileRepository;

    public MediaFileServiceImpl(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }
    private MediaFile addMediaFile(String fileName, String mimeType, String filePath, String parentId, InputStream inputStream) {
        if(mediaFileRepository.findByParentIdAndFileName(parentId, fileName) != null){
            return mediaFileRepository.findByParentIdAndFileName(parentId, fileName);
        }
        Store.saveFile(inputStream,filePath,fileName,mimeType);
        return mediaFileRepository.save(MediaFile.builder().filePath(filePath).fileName(fileName).mimeType(mimeType).parentId(parentId).build());
    }

    @Override
    public MediaFile addFile(String fileName, String mimeType, String parentId, InputStream is) {
        return null;
    }

    @Override
    public MediaFile addFile(String fileName, String mimeType, InputStream is) {
        return null;
    }

    @Override
    public String findPath(String fileId) {
        return "";
    }

    @Override
    public String createPath(String parentId) {
        return "";
    }

    @Override
    public void deleteFile(String fileId) {

    }
}
