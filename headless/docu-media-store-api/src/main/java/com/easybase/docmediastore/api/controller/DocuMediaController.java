/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.docmediastore.api.controller;

import com.easybase.store.StoreUtil;
import com.easybase.store.processor.base.BaseFileProcessor;
import com.easybase.store.processor.factory.FileProcessorFactory;

import java.io.File;
import java.io.IOException;

import lombok.RequiredArgsConstructor;

import org.apache.tika.exception.TikaException;

import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/fs")
@RequiredArgsConstructor
@RestController
public class DocuMediaController {

	@PostMapping("/{id}")
	public void uploadFile(
			@RequestParam("file") MultipartFile file, @PathVariable long id)
		throws IOException, SAXException, TikaException {

		_storeUtil.perFormChecks(_allowedExtensions, _maxFileSize, file);

		long fileId = 12345;

		File destination = _storeUtil.saveOriginalFile(_path, file);

		BaseFileProcessor fileProcessor =
			_fileProcessorFactory.getFileProcessor(file);

		fileProcessor.process(destination, _path + "/" + fileId);
	}

	@Value("${easy-base.docmediastore.file.included}")
	private String _allowedExtensions;

	private final FileProcessorFactory _fileProcessorFactory;

	@Value("${easy-base.docmediastore.file.size}")
	private String _maxFileSize;

	@Value("${easy-base.docmediastore.root.folder}")
	private String _path;

	private final StoreUtil _storeUtil;

}