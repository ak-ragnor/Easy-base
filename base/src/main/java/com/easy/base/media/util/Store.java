package com.easy.base.media.util;

import com.easy.base.entity.MediaFolder;
import com.easy.base.media.dto.FolderDto;
import com.easy.base.service.MediaFileService;
import com.easy.base.service.MediaFolderService;
import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Store {
    private static final String rootDir="upload";

    public static boolean saveFile(InputStream inputStream,String filePath,String fileName, String mimeType){
        try{
        File root = new File(rootDir+filePath);
        boolean result = root.mkdirs();
        Path file = Paths.get(rootDir+filePath, fileName);
        // Create and write the file
        FileOutputStream outputStream = new FileOutputStream(file.toFile());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String createFolder(String folderName, String parentId, MediaFolderService mediaFolderService) {
        return addFolder(folderName,parentId,mediaFolderService).getFolderId();
    }
    public static MediaFolder addFolder(String folderName,String parentId, MediaFolderService mediaFolderService){
        return parentId == null || parentId.isBlank()?mediaFolderService.addFolder(folderName):mediaFolderService.addFolder(folderName,parentId);
    }

    public static List<FolderDto> getFoldesInFolder(String folderId,MediaFolderService mediaFolderService) {
        return listOfFolders(folderId,mediaFolderService).stream().map(folder->{
            return FolderDto.builder().folderId(folder.getFolderId()).folderName(folder.getFolderName()).build();
        }).collect(Collectors.toList());
    }

    private static List<MediaFolder> listOfFolders(String folderId, MediaFolderService mediaFolderService) {
        return folderId == null?mediaFolderService.findByparentId(""):mediaFolderService.findByparentId(folderId);
    }

    public static void deleteFolderContent(String folderId, MediaFolderService mediaFolderService) {
        mediaFolderService.deleteFolders(folderId);
    }
    public static void deleteFromServer(String path) throws IOException {
        File rootDerectory = FileUtils.getFile(rootDir);
        File child = new File(rootDir+path);
        if(FileUtils.directoryContains(rootDerectory,child)){
            FileUtils.deleteQuietly(child);
        }
    }
}
