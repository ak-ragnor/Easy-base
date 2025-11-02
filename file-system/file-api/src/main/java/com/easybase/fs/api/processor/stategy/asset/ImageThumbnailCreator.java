package com.easybase.fs.api.processor.stategy.asset;

import com.easybase.fs.api.processor.base.BaseAssetCreator;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

/**
 * @author Saura
 * Date:01/11/25
 * Time:8:27 pm
 */
public class ImageThumbnailCreator implements BaseAssetCreator {
    @Override
    public void createAsset(MultipartFile file, String path) throws IOException {
        InputStream io = file.getInputStream();

        BufferedImage originalImage = ImageIO.read(io);

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File destination = new File(dir, Objects.requireNonNull(file.getOriginalFilename()));
        try (OutputStream out = new FileOutputStream(destination)) {
            io.transferTo(out);
        }

        BufferedImage preview = resizeImage(originalImage, 800);
        try(OutputStream previewFile = new FileOutputStream(path+"/"+file.getName()+"_preview.png")) {
            ImageIO.write(preview, "png", previewFile);
        }

        BufferedImage thumbnail = resizeImage(originalImage, 150);
        try(OutputStream thumbnailFile = new FileOutputStream(path+"/"+file.getName()+"_thumbnail.png")) {
            ImageIO.write(thumbnail, "png", thumbnailFile);
        }

    }
    private static BufferedImage resizeImage(BufferedImage original, int targetWidth) {
        double aspectRatio = (double) original.getHeight() / original.getWidth();
        int targetHeight = (int) (targetWidth * aspectRatio);

        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return resized;
    }

}
