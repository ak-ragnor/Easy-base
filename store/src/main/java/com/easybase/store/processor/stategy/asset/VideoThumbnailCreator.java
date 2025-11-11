/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor.stategy.asset;

import com.easybase.store.processor.base.BaseAssetCreator;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * @author Saura
 */
@Service
@Slf4j
public class VideoThumbnailCreator implements BaseAssetCreator {

	@Override
	public void createAsset(File file, String path) {
	}

}