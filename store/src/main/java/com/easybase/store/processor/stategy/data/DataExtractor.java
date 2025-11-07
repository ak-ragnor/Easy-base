/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor.stategy.data;

import com.easybase.store.processor.base.BaseDataExtractor;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 * @author Saura
 */
@Slf4j
@Component
public class DataExtractor implements BaseDataExtractor {

	@Override
	@Async("globalExecutor")
	public void extract(InputStream io)
		throws IOException, SAXException, TikaException {

		AutoDetectParser parser = new AutoDetectParser();

		Metadata metadata = new Metadata();

		parser.parse(io, new BodyContentHandler(), metadata);

		Map<String, String> data = new HashMap<>(
			(int)(metadata.size() / 0.75) + 1);

		for (String name : metadata.names()) {
			log.info("data is "+ name+" : "+metadata.get(name));
			data.put(name, metadata.get(name));
		}

	}
	private static final Logger log = LoggerFactory.getLogger(DataExtractor.class.getName());

}