/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.fs.api.processor;

import com.easybase.fs.api.processor.base.BaseDataExtractor;
import com.easybase.fs.api.processor.base.BaseFileProcessor;

import java.io.IOException;

import org.apache.tika.exception.TikaException;

import org.springframework.web.multipart.MultipartFile;

import org.xml.sax.SAXException;

/**
 * @author Saura
 */
public class DefaultFileProcessor implements BaseFileProcessor {

	BaseDataExtractor dataExtractor;

	public DefaultFileProcessor(BaseDataExtractor _dataExtractor) {
		this.dataExtractor = _dataExtractor;
	}

	@Override
	public void process(MultipartFile file, String path)
		throws IOException, SAXException, TikaException {

		dataExtractor.extract(file.getInputStream());
	}

}