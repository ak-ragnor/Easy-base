/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.fs.api.processor.base;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Saura
 */
public interface BaseAssetCreator {

	public void createAsset(MultipartFile file, String path) throws IOException;

}