/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor.stategy.asset;

import com.easybase.store.StoreUtil;
import com.easybase.store.processor.base.BaseAssetCreator;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Locale;
import java.util.Objects;

import javax.imageio.ImageIO;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Saura
 */
@AllArgsConstructor
@Service
@Slf4j
public class ImageThumbnailCreator implements BaseAssetCreator {

	@Async("globalExecutor")
	@Override
	public void createAsset(File file, String path) throws IOException {
		try {
			String fileOriginalName = Objects.requireNonNull(file.getName());

			String extension = _storeUtil.extractExtension(fileOriginalName);
			String fileBaseName = fileOriginalName.substring(
				0, fileOriginalName.lastIndexOf('.'));

			BufferedImage originalImage;

			try (InputStream io = new FileInputStream(file)) {
				originalImage = ImageIO.read(io);
			}

			if (originalImage == null) {
				throw new IOException(
					"Unsupported or corrupted image format for file: " +
						fileOriginalName);
			}

			Path baseDir = Paths.get(System.getProperty("user.home"), path);

			Files.createDirectories(baseDir);

			BufferedImage preview = _resizeImage(originalImage, _PREVIEW_WIDTH);

			Path resolvedPreview = baseDir.resolve(
				fileBaseName + "_preview." + extension);

			_writeImage(preview, extension, resolvedPreview.toFile());

			BufferedImage thumbnail = _resizeImage(
				originalImage, _THUMBNAIL_WIDTH);

			Path resolvedThumbnail = baseDir.resolve(
				fileBaseName + "_thumbnail." + extension);

			_writeImage(thumbnail, extension, resolvedThumbnail.toFile());

			log.info("Assets created successfully for {}", fileOriginalName);
		}
		catch (IOException ioException) {
			log.error(
				"Failed to process image file: {}", file.getName(),
				ioException);

			throw ioException;
		}
		finally {
			if (!file.delete()) {
				log.warn(
					"Failed to delete temp file: {}", file.getAbsolutePath());

				file.deleteOnExit();
			}
		}
	}

	private String _normalizeFormat(String extension) {
		switch (extension.toLowerCase(Locale.ROOT)) {
			case "jpeg":
			case "jpg":
				return "jpg";
			case "png":
			case "bmp":
			case "gif":
			case "wbmp":
			case "tif":
			case "tiff":
				return extension.toLowerCase(Locale.ROOT);
			case "webp":
				return "webp";
			default:
				return "png";
		}
	}

	private BufferedImage _resizeImage(
		BufferedImage original, int targetWidth) {

		if (original == null) {
			throw new IllegalArgumentException("Original image is null");
		}

		double aspectRatio = (double)original.getHeight() / original.getWidth();

		int targetHeight = (int)(targetWidth * aspectRatio);

		int imageType = original.getType();

		if (imageType == 0) {
			imageType = BufferedImage.TYPE_INT_RGB;
		}

		BufferedImage resized = new BufferedImage(
			targetWidth, targetHeight, imageType);

		Graphics2D g2d = resized.createGraphics();

		g2d.setRenderingHint(
			RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(
			RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
		g2d.dispose();

		return resized;
	}

	private void _writeImage(
			BufferedImage image, String extension, File outFile)
		throws IOException {

		File parentFile = outFile.getParentFile();

		parentFile.mkdirs();

		String format = _normalizeFormat(extension);

		boolean written = ImageIO.write(image, format, outFile);

		if (!written) {
			log.warn(
				"No ImageWriter found for {} — writing as PNG instead", format);

			ImageIO.write(image, "png", outFile);
		}
	}

	private static final int _PREVIEW_WIDTH = 800;

	private static final int _THUMBNAIL_WIDTH = 150;

	private final StoreUtil _storeUtil;

}