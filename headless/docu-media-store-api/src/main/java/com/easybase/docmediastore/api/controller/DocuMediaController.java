/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.docmediastore.api.controller;

import com.easybase.store.processor.base.BaseFileProcessor;
import com.easybase.store.processor.factory.FileProcessorFactory;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.apache.tika.exception.TikaException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.xml.sax.SAXException;

/**
 * @author Saura
 */
@RequestMapping("/easy-base/api/fs")
@RequiredArgsConstructor
@RestController
@Slf4j
public class DocuMediaController {

	@PostMapping("/{parentId}")
	public void uploadFile(
			@RequestParam("file") MultipartFile file,
			@PathVariable(name = "parentId", required = true, value = "0") long
				id)
		throws IOException, SAXException, TikaException {

		BaseFileProcessor fileProcessor = FileProcessorFactory.getFileProcessor(
			file);

		String path = "/file_system";
		long fileId = 12345;

		fileProcessor.process(file, path + "/" + fileId);
	}

}