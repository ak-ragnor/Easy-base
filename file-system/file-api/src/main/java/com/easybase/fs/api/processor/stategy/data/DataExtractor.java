/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.fs.api.processor.stategy.data;

import com.easybase.fs.api.processor.base.BaseDataExtractor;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.SAXException;

/**
 * @author Saura
 */
@Slf4j
public class DataExtractor implements BaseDataExtractor {

	@Override
	public Map<String, String> extract(InputStream io)
		throws IOException, SAXException, TikaException {

		AutoDetectParser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();

		parser.parse(io, handler, metadata);

		Map<String, String> data = new HashMap<>(
			(int)(metadata.size() / 0.75) + 1);

		for (String name : metadata.names()) {
			log.info(name + ": " + metadata.get(name));
			data.put(name, metadata.get(name));
		}

		return data;
	}

}