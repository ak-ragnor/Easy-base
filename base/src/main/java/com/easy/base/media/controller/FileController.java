package com.easy.base.media.controller;

import com.easy.base.media.dto.FileDetailsDto;
import com.easy.base.media.util.Store;
import com.easy.base.service.MediaFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final MediaFileService mediaFileService;

    public FileController(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("files") List<MultipartFile> files, @RequestParam(required = false) String folderId){
        return ResponseEntity.ok(Store.createFile(files, mediaFileService,folderId));
    }
    @GetMapping
    public FileDetailsDto getFileDetails(@RequestParam String fileId){
        return Store.fileDetails(fileId, mediaFileService);
    }
    @GetMapping("/folder")
    public List<FileDetailsDto> getFilesInFolder(@RequestParam String folderId){
        return Store.filesDetails(folderId, mediaFileService);
    }
    @DeleteMapping
    public void deleteFile(@RequestParam String fileId) throws IOException {
         Store.deleteFile(fileId,mediaFileService);
    }
}
