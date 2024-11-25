package com.easy.base.service;


import com.easy.base.model.MediaFolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MediaFolderService {
    public String findPath(String folderId);
    public String createFolderPath(String parentId);
    public void deleteFolders(String folderId);
    public MediaFolder addFolder(String name, String parentId);
    public MediaFolder addFolder(String name);
    public Page<MediaFolder> findByparentId(String patrentId, Pageable pageable);
    public List<MediaFolder> findByparentId(String patrentId);
    public MediaFolder findByParentIdAndFolderName(String parentId, String folderName);
}
