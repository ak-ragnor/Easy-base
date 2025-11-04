/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Saura
 */
public enum FileCategory {

	ARCHIVE(
		Pattern.compile(
			"^(application/(zip|x-zip-compressed|x-rar-compressed|x-7z-compressed|gzip|x-tar))$")),
	AUDIO(Pattern.compile("^audio/.*")),
	DOCUMENT(
		Pattern.compile(
			"^(application/msword|application/vnd\\.openxmlformats-officedocument\\.wordprocessingml\\.document|text/plain|text/rtf)$")),
	IMAGE(Pattern.compile("^image/.*")),
	PDF(Pattern.compile("^application/pdf$")),
	PRESENTATION(
		Pattern.compile(
			"^(application/vnd\\.ms-powerpoint|application/vnd\\.openxmlformats-officedocument\\.presentationml\\.presentation|application/vnd\\.oasis\\.opendocument\\.presentation)$")),
	SPREADSHEET(
		Pattern.compile(
			"^(application/vnd\\.ms-excel|application/vnd\\.openxmlformats-officedocument\\.spreadsheetml\\.sheet|application/vnd\\.oasis\\.opendocument\\.spreadsheet)$")),
	UNKNOWN(Pattern.compile(".*")), VIDEO(Pattern.compile("^video/.*"));

	public static FileCategory fromMimeType(String mimeType) {
		if (mimeType == null) {
			return UNKNOWN;
		}

		String lowerMimeType = mimeType.toLowerCase();

		for (FileCategory category : values()) {
			Matcher matcher = category.pattern.matcher(lowerMimeType);

			if (matcher.matches()) {
				return category;
			}
		}

		return UNKNOWN;
	}

	FileCategory(Pattern pattern) {
		this.pattern = pattern;
	}

	private final Pattern pattern;

}