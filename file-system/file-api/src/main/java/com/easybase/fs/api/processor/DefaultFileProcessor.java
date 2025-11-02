package com.easybase.fs.api.processor;

import com.easybase.fs.api.processor.base.BaseDataExtractor;
import com.easybase.fs.api.processor.base.BaseFileProcessor;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Saura
 * Date:02/11/25
 * Time:11:39 am
 */
public class DefaultFileProcessor implements BaseFileProcessor {
    BaseDataExtractor dataExtractor;

    public DefaultFileProcessor(BaseDataExtractor _dataExtractor){
        this.dataExtractor = _dataExtractor;
    }

    @Override
    public void process(MultipartFile file, String path) throws TikaException, IOException, SAXException {
        dataExtractor.extract(file.getInputStream());
    }
}
