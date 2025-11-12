/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor.base;

import java.io.File;
import java.io.IOException;

import org.apache.tika.exception.TikaException;

import org.xml.sax.SAXException;

/**
 * @author Saura
 */
public interface BaseFileProcessor {

	public void process(File file, String path)
		throws IOException, SAXException, TikaException;

}