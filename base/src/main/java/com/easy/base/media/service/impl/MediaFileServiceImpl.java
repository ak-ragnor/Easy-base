package com.easy.base.media.service.impl;

import com.easy.base.counter.CounterService;
import com.easy.base.media.entity.MediaFile;
import com.easy.base.media.repository.MediaFileRepository;
import com.easy.base.media.service.MediaFileService;
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
        MediaFile mediaFile = mediaFileRepository.findByParentIdAndFileName(parentId, fileName);
        if(mediaFile != null){
            return mediaFile;
        }
        mediaFile = MediaFile.builder().fileId(counterService.getGeneratedCounter(MediaFile.class.getName())).filePath(filePath).fileName(fileName).mimeType(mimeType).parentId(parentId).build();
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
