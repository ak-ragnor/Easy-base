package com.easybase.fs.api.controller;

import com.easybase.fs.api.processor.base.BaseFileProcessor;
import com.easybase.fs.api.processor.factory.FileProcessorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Saura
 * Date:01/11/25
 * Time:1:09 pm
 */
@RequestMapping("/easy-base/api/fs")
@RequiredArgsConstructor
@RestController
@Slf4j
public class FsFileController {

    @PostMapping("/{parentId}")
    public void uploadFile(@RequestParam("file") MultipartFile file, @PathVariable(name = "parentId",value = "0", required = true) long id) throws IOException, TikaException, SAXException {

        //Extract path from the folder id
        String path = "/file_system";
        //perform all necessary checks
        //save to db and return back the id
        long fileId = 12345;
        BaseFileProcessor fileProcessor = FileProcessorFactory.getFileProcessor(file);
        fileProcessor.process(file,path+"/"+fileId);
    }
}