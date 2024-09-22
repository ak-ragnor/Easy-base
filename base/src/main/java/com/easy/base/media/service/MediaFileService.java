package com.easy.base.media.service;

import com.easy.base.media.entity.MediaFile;

import java.io.InputStream;

public interface MediaFileService {
    public MediaFile addMediaFile(String fileName, String mimeType, String filePath, long parentId, InputStream inputStream);
    public MediaFile deleteMediaFile (long fileId);
}
