package com.easy.base.service;


import com.easy.base.model.MediaFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;

public interface MediaFileService {
    public MediaFile addFile(String fileName, String mimeType, String parentId, InputStream is);
    public MediaFile addFile(String fileName, String mimeType, InputStream is);
    public String findPath(String fileId);
    public String createPath(String parentId);
    public Page<MediaFile> findByParentId(String parentId, Pageable pageable);
    public MediaFile findById(String fileId);
    public void delete(String id);
}
