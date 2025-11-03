/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.fs.api.processor.base;

import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

import org.apache.tika.exception.TikaException;

import org.xml.sax.SAXException;

/**
 * @author Saura
 */
public interface BaseDataExtractor {

	public Map<String, String> extract(InputStream io)
		throws IOException, SAXException, TikaException;

}