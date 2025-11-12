/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor.factory;

import com.easybase.store.processor.DefaultFileProcessor;
import com.easybase.store.processor.FileCategory;
import com.easybase.store.processor.RegularFileProcessor;
import com.easybase.store.processor.base.BaseAssetCreator;
import com.easybase.store.processor.base.BaseDataExtractor;
import com.easybase.store.processor.base.BaseFileProcessor;
import com.easybase.store.processor.stategy.asset.DocumentThumbnailCreator;
import com.easybase.store.processor.stategy.asset.ExcelThumbnailCreator;
import com.easybase.store.processor.stategy.asset.ImageThumbnailCreator;
import com.easybase.store.processor.stategy.asset.PdfThumbnailCreator;
import com.easybase.store.processor.stategy.asset.VideoThumbnailCreator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Saura
 */
@Component
@RequiredArgsConstructor
public class FileProcessorFactory {

	public BaseFileProcessor getFileProcessor(MultipartFile file) {
		String mimeType = file.getContentType();

		return _createFileProcessor(_getAssetCreator(mimeType));
	}

	private BaseFileProcessor _createFileProcessor(
		BaseAssetCreator assetCreator) {

		if (assetCreator == null) {
			return new DefaultFileProcessor(dataExtractor);
		}

		return new RegularFileProcessor(dataExtractor, assetCreator);
	}

	private BaseAssetCreator _getAssetCreator(String mimeType) {
		FileCategory category = FileCategory.fromMimeType(mimeType);

		switch (category) {
			case IMAGE:
				return imageThumbnailCreator;
			case VIDEO:
				return videoThumbnailCreator;
			case PDF:
				return pdfThumbnailCreator;
			case DOCUMENT:
				return documentThumbnailCreator;
			case SPREADSHEET:
				return excelThumbnailCreator;
			default:
				return null;
		}
	}

	private final BaseDataExtractor dataExtractor;
	private final DocumentThumbnailCreator documentThumbnailCreator;
	private final ExcelThumbnailCreator excelThumbnailCreator;
	private final ImageThumbnailCreator imageThumbnailCreator;
	private final PdfThumbnailCreator pdfThumbnailCreator;
	private final VideoThumbnailCreator videoThumbnailCreator;

}