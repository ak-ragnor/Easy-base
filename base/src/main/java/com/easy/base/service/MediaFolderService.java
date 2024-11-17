package com.easy.base.service;

import com.easy.base.entity.MediaFolder;

import java.util.List;

public interface MediaFolderService {
    public String findPath(String folderId);
    public String createFolderPath(String parentId);
    public void deleteFolders(String folderId);
    public MediaFolder addFolder(String name, String parentId);
    public MediaFolder addFolder(String name);
    public List<MediaFolder> findByparentId(String patrentId);
    public MediaFolder findByParentIdAndFolderName(String parentId, String folderName);
}
