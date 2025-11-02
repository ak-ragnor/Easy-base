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
import com.easybase.fs.api.processor.stategy.data.DataExtractor;
import com.easybase.fs.api.processor.stategy.asset.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Saura
 */
public class FileProcessorFactory {
    public static BaseFileProcessor getFileProcessor(MultipartFile file){

        String mimeType = file.getContentType();
        BaseAssetCreator assetCreator = getAssetCreator(mimeType);

        return createFileProcessor(assetCreator);
    }

    private static BaseFileProcessor createFileProcessor(BaseAssetCreator assetCreator) {
        if (assetCreator == null) return new DefaultFileProcessor(new DataExtractor());
        return new RegularFileProcessor(new DataExtractor(), assetCreator);
    }

    private static BaseAssetCreator getAssetCreator(String mimeType) {
        FileCategory category = FileCategory.fromMimeType(mimeType);

        return switch (category) {
            case IMAGE -> new ImageThumbnailCreator();
            case VIDEO -> new VideoThumbnailCreator();
            case PDF -> new PdfThumbnailCreator();
            case DOCUMENT -> new DocumentThumbnailCreator();
            case SPREADSHEET -> new ExcelThumbnailCreator();
            default -> null;
        };
    }

}
