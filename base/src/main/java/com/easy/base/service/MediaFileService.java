package com.easy.base.service;

import com.easy.base.entity.MediaFile;

import java.io.InputStream;

public interface MediaFileService {
    public MediaFile
    addMediaFile(String fileName, String mimeType, String filePath, long parentId, InputStream inputStream);
    public MediaFile deleteMediaFile (long fileId);
}
