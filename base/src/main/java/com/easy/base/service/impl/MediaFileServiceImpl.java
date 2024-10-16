package com.easy.base.service.impl;

import com.easy.base.config.counter.CounterService;
import com.easy.base.entity.MediaFile;
import com.easy.base.repository.MediaFileRepository;
import com.easy.base.service.MediaFileService;
import com.easy.base.media.util.Store;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MediaFileServiceImpl implements MediaFileService {
    private final MediaFileRepository mediaFileRepository;
    private final CounterService counterService;

    public MediaFileServiceImpl(MediaFileRepository mediaFileRepository, CounterService counterService) {
        this.mediaFileRepository = mediaFileRepository;
        this.counterService = counterService;
    }

    @Override
    public MediaFile addMediaFile(String fileName, String mimeType, String filePath, long parentId, InputStream inputStream) {
        if(mediaFileRepository.findByParentIdAndFileName(parentId, fileName) != null){
            return mediaFileRepository.findByParentIdAndFileName(parentId, fileName);
        }
        MediaFile mediaFile = MediaFile.builder().filePath(filePath).fileName(fileName).mimeType(mimeType).parentId(parentId).build();
        System.out.println(mediaFile);
        mediaFile = mediaFileRepository.save(mediaFile);
        System.out.println(mediaFile);
        Store.saveFile(inputStream,filePath,fileName,mimeType);
        return mediaFile;
//        return null;
    }

    @Override
    public MediaFile deleteMediaFile(long fileId) {
        return null;
    }
}
