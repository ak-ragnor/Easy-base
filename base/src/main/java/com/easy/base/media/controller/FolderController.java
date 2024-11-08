package com.easy.base.media.controller;

import com.easy.base.media.dto.FolderDto;
import com.easy.base.media.util.Store;
import com.easy.base.service.MediaFileService;
import com.easy.base.service.MediaFolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {
    private final MediaFolderService mediaFolderService;
    private final MediaFileService mediaFileService;

    public FolderController(MediaFolderService mediaFolderService, MediaFileService mediaFileService) {
        this.mediaFolderService = mediaFolderService;
        this.mediaFileService = mediaFileService;
    }

    @PostMapping
    public ResponseEntity<String> createFolder(@RequestBody FolderDto folderDto){
        return ResponseEntity.ok(Store.createFolder(folderDto.getFolderName(),folderDto.getParentId(),mediaFolderService));
    }
    @GetMapping
    public List<FolderDto> getAllFolders(@RequestParam(required = false) String folderId){
        return Store.getFoldesInFolder(folderId,mediaFolderService);
    }
    @DeleteMapping
    public void deleteFolders(@RequestParam String folderId){
        Store.deleteFolderContent(folderId, mediaFolderService, mediaFileService);
    }
}
