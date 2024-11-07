package com.easy.base.media.controller;

import com.easy.base.media.dto.FolderDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {
    @PostMapping
    public ResponseEntity<String> createFolder(@RequestBody FolderDto folderDto){
        return null;
    }
    @GetMapping
    public List<FolderDto> getAllFolders(@RequestParam(required = false) String folderId){
        return null;
    }
    @DeleteMapping
    public void deleteFolders(@RequestParam String folderId){}
}
