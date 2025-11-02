package com.easybase.fs.api.processor.base;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Saura
 * Date:01/11/25
 * Time:7:40 pm
 */
public interface BaseAssetCreator {

    public void createAsset(MultipartFile file, String path) throws IOException;
}
