/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor;

import com.easybase.store.processor.base.BaseAssetCreator;
import com.easybase.store.processor.base.BaseDataExtractor;
import com.easybase.store.processor.base.BaseFileProcessor;

import java.io.IOException;

import org.apache.tika.exception.TikaException;

import org.springframework.web.multipart.MultipartFile;

import org.xml.sax.SAXException;

/**
 * @author Saura
 */
public class RegularFileProcessor implements BaseFileProcessor {

	public RegularFileProcessor(
		BaseDataExtractor dataExtractor, BaseAssetCreator assetCreator) {

		this.dataExtractor = dataExtractor;
		this.assetCreator = assetCreator;
	}

	@Override
	public void process(MultipartFile file, String path)
		throws IOException, SAXException, TikaException {

		dataExtractor.extract(file.getInputStream());
		assetCreator.createAsset(file, path);
	}

	private BaseAssetCreator assetCreator;
	private BaseDataExtractor dataExtractor;

}