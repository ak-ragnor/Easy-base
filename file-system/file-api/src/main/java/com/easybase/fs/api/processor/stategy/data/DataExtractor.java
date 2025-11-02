package com.easybase.fs.api.processor.stategy.data;

import com.easybase.fs.api.processor.base.BaseDataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Saura
 * Date:01/11/25
 * Time:8:25 pm
 */

@Slf4j
public class DataExtractor implements BaseDataExtractor {


    @Override
    public Map<String, String> extract(InputStream io) throws TikaException, IOException, SAXException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();

        parser.parse(io, handler, metadata);

       Map<String,String> data = new HashMap<>((int)(metadata.size()/0.75)+1);
        for (String name : metadata.names()) {
            log.info(name + ": " + metadata.get(name));
            data.put(name,metadata.get(name));
        }
        return data;
    }
}
