package com.easy.base.service;

import com.easy.base.entity.MediaFile;

import java.io.InputStream;

public interface MediaFileService {
    public MediaFile addFile(String fileName, String mimeType, String parentId, InputStream is);
    public MediaFile addFile(String fileName, String mimeType, InputStream is);
    public String findPath(String fileId);
    public String createPath(String parentId);
}
