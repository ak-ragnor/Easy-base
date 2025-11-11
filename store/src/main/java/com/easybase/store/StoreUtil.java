/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store;

import com.easybase.common.exception.FileValidationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Locale;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Saura
 */
@Component
public class StoreUtil {

	public String extractExtension(String fileName) {
		int i = fileName.lastIndexOf('.');

		return fileName.substring(
			i + 1
		).toLowerCase(
			Locale.ROOT
		);
	}

	public void perFormChecks(
		String allowedExtensions, String maxFileSize, MultipartFile file) {

		String fileExtension = extractExtension(file.getOriginalFilename());
		long size = file.getSize();

		if (!allowedExtensions.contains(fileExtension))

			throw new FileValidationException("File type is not allowed");
			else if (size > Long.parseLong(maxFileSize))

				throw new FileValidationException(
					"File size exceeded the allowed size");
	}

	public File saveOriginalFile(String path, MultipartFile file)
		throws IOException {

		Path baseDir = Paths.get(System.getProperty("user.home"), path);

		Files.createDirectories(baseDir);

		File destination = baseDir.resolve(
			file.getOriginalFilename()
		).toFile();

		try (OutputStream os = new FileOutputStream(destination);
			InputStream is = file.getInputStream()) {

			is.transferTo(os);
		}

		return destination;
	}

}