/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor.stategy.asset;

import com.easybase.store.processor.base.BaseAssetCreator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author Saura
 */
@Service
public class ExcelThumbnailCreator implements BaseAssetCreator {

	@Override
	public void createAsset(File file, String path) {
	}

}