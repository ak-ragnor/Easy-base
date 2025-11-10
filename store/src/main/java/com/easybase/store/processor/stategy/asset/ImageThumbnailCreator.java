/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.store.processor.stategy.asset;

import com.easybase.store.StoreUtil;
import com.easybase.store.processor.base.BaseAssetCreator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Saura
 */
@Slf4j
@Service
@AllArgsConstructor
public class ImageThumbnailCreator implements BaseAssetCreator {

	private static final int PREVIEW_WIDTH = 800;
	private static final int THUMBNAIL_WIDTH = 150;

	@Override
	@Async("globalExecutor")
	public void createAsset(File file, String path) throws IOException {
		try {
			String fileOriginalName = Objects.requireNonNull(file.getName());
			String extension = _storeUtil.extractExtension(fileOriginalName);
			String fileBaseName = fileOriginalName.substring(0, fileOriginalName.lastIndexOf('.'));

			BufferedImage originalImage;
			try (InputStream io = new FileInputStream(file)) {
				originalImage = ImageIO.read(io);
			}

			if (originalImage == null) {
				throw new IOException("Unsupported or corrupted image format for file: " + fileOriginalName);
			}

			Path baseDir = Paths.get(System.getProperty("user.home"), path);
			Files.createDirectories(baseDir);

			BufferedImage preview = _resizeImage(originalImage, PREVIEW_WIDTH);
			_writeImage(preview, extension, baseDir.resolve(fileBaseName + "_preview." + extension).toFile());

			BufferedImage thumbnail = _resizeImage(originalImage, THUMBNAIL_WIDTH);
			_writeImage(thumbnail, extension, baseDir.resolve(fileBaseName + "_thumbnail." + extension).toFile());

			log.info("Assets created successfully for {}", fileOriginalName);
		} catch (IOException exception) {
			log.error("Failed to process image file: {}", file.getName(), exception);
			throw exception;
		} finally {
			if (!file.delete()) {
				log.warn("Failed to delete temp file: {}", file.getAbsolutePath());
				file.deleteOnExit();
			}
		}
	}

	private BufferedImage _resizeImage(BufferedImage original, int targetWidth) {
		if (original == null) {
			throw new IllegalArgumentException("Original image is null");
		}

		double aspectRatio = (double) original.getHeight() / original.getWidth();
		int targetHeight = (int) (targetWidth * aspectRatio);

		// choose type based on source
		int imageType = original.getType() == 0 ? BufferedImage.TYPE_INT_RGB : original.getType();
		BufferedImage resized = new BufferedImage(targetWidth, targetHeight, imageType);

		Graphics2D g2d = resized.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
		g2d.dispose();

		return resized;
	}

	private void _writeImage(BufferedImage image, String extension, File outFile) throws IOException {
		outFile.getParentFile().mkdirs();

		String format = _normalizeFormat(extension);
		boolean written = ImageIO.write(image, format, outFile);

		if (!written) {
			log.warn("No ImageWriter found for {} — writing as PNG instead", format);
			ImageIO.write(image, "png", outFile);
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
	private final StoreUtil _storeUtil;
}
