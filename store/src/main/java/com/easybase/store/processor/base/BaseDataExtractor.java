/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor.base;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;

import org.xml.sax.SAXException;

/**
 * @author Saura
 */
public interface BaseDataExtractor {

	public void extract(InputStream io)
		throws IOException, SAXException, TikaException;

}