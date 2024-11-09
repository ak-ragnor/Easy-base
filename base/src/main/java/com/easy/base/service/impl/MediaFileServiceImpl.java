package com.easy.base.service.impl;

import com.easy.base.entity.MediaFile;
import com.easy.base.repository.MediaFileRepository;
import com.easy.base.repository.MediaFolderRepository;
import com.easy.base.service.MediaFileService;
import com.easy.base.media.util.Store;
import org.springframework.stereotype.Service;

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
    private MediaFile addMediaFile(String fileName, String mimeType, String filePath, String parentId, InputStream inputStream) {
        if(mediaFileRepository.findByParentIdAndFileName(parentId, fileName) != null){
            return mediaFileRepository.findByParentIdAndFileName(parentId, fileName);
        }
        Store.saveFile(inputStream,filePath,fileName,mimeType);
        return mediaFileRepository.save(MediaFile.builder().filePath(filePath).fileName(fileName).mimeType(mimeType).parentId(parentId).build());
    }

    @Override
    public MediaFile addFile(String fileName, String mimeType, String parentId, InputStream is) {
        return this.addMediaFile(fileName,mimeType,createPath(parentId),parentId,is);
    }

    @Override
    public MediaFile addFile(String fileName, String mimeType, InputStream is) {
        return this.addMediaFile(fileName,mimeType,"/","",is);
    }

    @Override
    public String findPath(String fileId) {
        return mediaFileRepository.findById(fileId).orElse(null).getFilePath();
    }

    @Override
    public String createPath(String parentId) {
        return mediaFolderRepository.findById(parentId).orElse(null).getFolderPath()+parentId+"/";
    }

    @Override
    public List<MediaFile> findByParentId(String parentId) {
        return mediaFileRepository.findByParentId(parentId);
    }
}
