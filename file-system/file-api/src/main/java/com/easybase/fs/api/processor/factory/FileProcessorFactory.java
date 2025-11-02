/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.fs.api.processor.factory;

import com.easybase.fs.api.processor.DefaultFileProcessor;
import com.easybase.fs.api.processor.FileCategory;
import com.easybase.fs.api.processor.RegularFileProcessor;
import com.easybase.fs.api.processor.base.BaseAssetCreator;
import com.easybase.fs.api.processor.base.BaseFileProcessor;
import com.easybase.fs.api.processor.stategy.asset.DocumentThumbnailCreator;
import com.easybase.fs.api.processor.stategy.asset.ExcelThumbnailCreator;
import com.easybase.fs.api.processor.stategy.asset.ImageThumbnailCreator;
import com.easybase.fs.api.processor.stategy.asset.PdfThumbnailCreator;
import com.easybase.fs.api.processor.stategy.asset.VideoThumbnailCreator;
import com.easybase.fs.api.processor.stategy.data.DataExtractor;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Saura
 */
public class FileProcessorFactory {

	public static BaseFileProcessor getFileProcessor(MultipartFile file) {
		String mimeType = file.getContentType();

		return _createFileProcessor(_getAssetCreator(mimeType));
	}

	private static BaseFileProcessor _createFileProcessor(
		BaseAssetCreator assetCreator) {

		if (assetCreator == null) {
			return new DefaultFileProcessor(new DataExtractor());
		}

		return new RegularFileProcessor(new DataExtractor(), assetCreator);
	}

	private static BaseAssetCreator _getAssetCreator(String mimeType) {
		FileCategory category = FileCategory.fromMimeType(mimeType);

		if (category == FileCategory.IMAGE) {
			return new ImageThumbnailCreator();
		}
		else if (category == FileCategory.VIDEO) {
			return new VideoThumbnailCreator();
		}
		else if (category == FileCategory.PDF) {
			return new PdfThumbnailCreator();
		}
		else if (category == FileCategory.DOCUMENT) {
			return new DocumentThumbnailCreator();
		}
		else if (category == FileCategory.SPREADSHEET) {
			return new ExcelThumbnailCreator();
		}

		return null;
	}

}