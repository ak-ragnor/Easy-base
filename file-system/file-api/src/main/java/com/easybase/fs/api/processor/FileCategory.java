/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.fs.api.processor;

import java.util.regex.Pattern;

/**
 * @author Saura
 */
public enum FileCategory {

	IMAGE(Pattern.compile("^image/.*")), VIDEO(Pattern.compile("^video/.*")),
	AUDIO(Pattern.compile("^audio/.*")),
	PDF(Pattern.compile("^application/pdf$")),
	DOCUMENT(
		Pattern.compile(
			"^(application/msword|application/vnd\\.openxmlformats-officedocument\\.wordprocessingml\\.document|text/plain|text/rtf)$")),
	SPREADSHEET(
		Pattern.compile(
			"^(application/vnd\\.ms-excel|application/vnd\\.openxmlformats-officedocument\\.spreadsheetml\\.sheet|application/vnd\\.oasis\\.opendocument\\.spreadsheet)$")),
	PRESENTATION(
		Pattern.compile(
			"^(application/vnd\\.ms-powerpoint|application/vnd\\.openxmlformats-officedocument\\.presentationml\\.presentation|application/vnd\\.oasis\\.opendocument\\.presentation)$")),
	ARCHIVE(
		Pattern.compile(
			"^(application/(zip|x-zip-compressed|x-rar-compressed|x-7z-compressed|gzip|x-tar))$")),
	UNKNOWN(Pattern.compile(".*")); // fallback

	private final Pattern pattern;

	FileCategory(Pattern pattern) {
		this.pattern = pattern;
	}

	public static FileCategory fromMimeType(String mimeType) {
		if (mimeType == null)

			return UNKNOWN;

		for (FileCategory category : values()) {
			if (category.pattern.matcher(
					mimeType.toLowerCase()
				).matches()) {

				return category;
			}
		}

		return UNKNOWN;
	}

}