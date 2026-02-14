/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor;

import com.easybase.store.processor.base.BaseAssetCreator;
import com.easybase.store.processor.base.BaseDataExtractor;
import com.easybase.store.processor.base.BaseFileProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;

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
	public void process(File file, String path)
		throws IOException, SAXException, TikaException {

		dataExtractor.extract(new FileInputStream(file));
		assetCreator.createAsset(file, path);
	}

	private final BaseAssetCreator assetCreator;
	private final BaseDataExtractor dataExtractor;

}