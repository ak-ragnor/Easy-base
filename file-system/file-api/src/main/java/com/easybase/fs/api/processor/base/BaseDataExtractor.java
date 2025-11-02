package com.easybase.fs.api.processor.base;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Saura
 * Date:01/11/25
 * Time:1:34 pm
 */
public interface BaseDataExtractor {
    public Map<String,String> extract(InputStream io) throws TikaException, IOException, SAXException;

}
