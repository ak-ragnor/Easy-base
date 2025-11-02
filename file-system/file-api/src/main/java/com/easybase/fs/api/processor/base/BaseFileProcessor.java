package com.easybase.fs.api.processor.base;

import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Saura
 * Date:01/11/25
 * Time:7:18 pm
 */
public interface BaseFileProcessor {

    public void process(MultipartFile file, String path) throws TikaException, IOException, SAXException;
}
