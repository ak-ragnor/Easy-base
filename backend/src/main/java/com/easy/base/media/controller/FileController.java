package com.easy.base.media.controller;

import com.easy.base.media.dto.FileDetailsDto;
import com.easy.base.media.dto.assembler.FileDtoAssembler;
import com.easy.base.media.util.Store;
import com.easy.base.service.MediaFileService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final MediaFileService mediaFileService;
    private final FileDtoAssembler fileDtoAssembler;
    private final PagedResourcesAssembler<FileDetailsDto> pagedResourcesAssembler;

    public FileController(MediaFileService mediaFileService, FileDtoAssembler fileDtoAssembler, PagedResourcesAssembler<FileDetailsDto> pagedResourcesAssembler) {
        this.mediaFileService = mediaFileService;
        this.fileDtoAssembler = fileDtoAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("files") List<MultipartFile> files, @RequestParam(required = false) String folderId){
        return ResponseEntity.ok(Store.createFile(files, mediaFileService,folderId));
    }
    @GetMapping
    public ResponseEntity<EntityModel<FileDetailsDto>> getFileDetails(@RequestParam String fileId){

        return ResponseEntity.ok(fileDtoAssembler.toModel(Store.fileDetails(fileId, mediaFileService)));
    }
    @GetMapping("/folder")
    public ResponseEntity<CollectionModel<EntityModel<FileDetailsDto>>> getFilesInFolder(@RequestParam String folderId, @PageableDefault(size = 20, sort = "id") Pageable pageable){

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(Store.filesDetails(folderId, pageable, mediaFileService),fileDtoAssembler));
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteFile(@RequestParam String fileId) throws IOException {
         Store.deleteFile(fileId,mediaFileService);
        return ResponseEntity.noContent().build();
    }
}
