package com.easybase.fs.controller;

import com.easybase.fs.Exceptions.FsNullFileException;
import com.easybase.fs.dto.ErrorResponse;
import com.easybase.fs.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Saurasish
 * @Date 14-09-2025
 */

@RequestMapping("/easy-base/api/fs")
@RequiredArgsConstructor
@RestController
@Slf4j
public class FsFileController {

    @PostMapping("/{parentId}")
    public ResponseEntity<Response> uploadFile(@RequestParam("file")MultipartFile file, @PathVariable(name = "parentId",value = "0", required = true) long id){
        try{
            if(file == null) throw new FsNullFileException(file.getName()+ " is null");
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(ErrorResponse.builder().reason(e.getMessage()).build());
        }
        return null;
    }
}
