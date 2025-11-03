/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor.base;

import java.io.IOException;

import org.apache.tika.exception.TikaException;

import org.springframework.web.multipart.MultipartFile;

import org.xml.sax.SAXException;

/**
 * @author Saura
 */
public interface BaseFileProcessor {

	public void process(MultipartFile file, String path)
		throws IOException, SAXException, TikaException;

}