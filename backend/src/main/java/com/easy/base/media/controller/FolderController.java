package com.easy.base.media.controller;

import com.easy.base.media.dto.assembler.FolderDtoAssembler;
import com.easy.base.media.util.Store;
import com.easy.base.media.dto.FolderDto;
import com.easy.base.service.MediaFileService;
import com.easy.base.service.MediaFolderService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/folders")
public class FolderController {
    private final MediaFolderService mediaFolderService;
    private final MediaFileService mediaFileService;
    private final FolderDtoAssembler folderDtoAssembler;
    private final PagedResourcesAssembler<FolderDto> pagedResourcesAssembler;

    public FolderController(MediaFolderService mediaFolderService, MediaFileService mediaFileService, FolderDtoAssembler folderDtoAssembler, PagedResourcesAssembler<FolderDto> pagedResourcesAssembler) {
        this.mediaFolderService = mediaFolderService;
        this.mediaFileService = mediaFileService;
        this.folderDtoAssembler = folderDtoAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping
    public ResponseEntity<EntityModel<FolderDto>> createFolder(@RequestBody FolderDto folderDto){
        String id = Store.createFolder(folderDto.getFolderName(),folderDto.getParentId(),mediaFolderService);
        folderDto.setFolderId(id);
        return ResponseEntity.ok(folderDtoAssembler.toModel(folderDto));
    }
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<FolderDto>>> getAllFolders(@RequestParam(required = false) String folderId, @PageableDefault(size = 20, sort = "id") Pageable pageable){

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(Store.getFoldesInFolder(folderId, pageable, mediaFolderService),folderDtoAssembler));
    }
    @DeleteMapping
    public  ResponseEntity<Void> deleteFolder(@RequestParam String folderId) throws IOException {
        Store.deleteFromServer(mediaFileService.createPath(folderId));
        Store.deleteFolderContent(folderId, mediaFolderService);
        return ResponseEntity.noContent().build();
    }
}
