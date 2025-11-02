/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.fs.api.controller;

import com.easybase.fs.api.processor.base.BaseFileProcessor;
import com.easybase.fs.api.processor.factory.FileProcessorFactory;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.apache.tika.exception.TikaException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.xml.sax.SAXException;

/**
 * @author Saura
 */
@RequestMapping("/easy-base/api/fs")
@RequiredArgsConstructor
@RestController
@Slf4j
public class FsFileController {

	@PostMapping("/{parentId}")
	public void uploadFile(
			@RequestParam("file") MultipartFile file,
			@PathVariable(name = "parentId", required = true, value = "0") long
				id)
		throws IOException, SAXException, TikaException {

		//Extract path from the folder id
		String path = "/file_system";
		//perform all necessary checks
		//save to db and return back the id
		long fileId = 12345;
		BaseFileProcessor fileProcessor = FileProcessorFactory.getFileProcessor(
			file);

		fileProcessor.process(file, path + "/" + fileId);
	}

}