package com.easybase.fs.api.processor;

import com.easybase.fs.api.processor.base.BaseAssetCreator;
import com.easybase.fs.api.processor.base.BaseDataExtractor;
import com.easybase.fs.api.processor.base.BaseFileProcessor;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Saura
 * Date:01/11/25
 * Time:7:21 pm
 */
public class RegularFileProcessor implements BaseFileProcessor {

    BaseDataExtractor dataExtractor;
    BaseAssetCreator assetCreator;

    public RegularFileProcessor(BaseDataExtractor _dataExtractor, BaseAssetCreator _assetCreator){
        this.assetCreator = _assetCreator;
        this.dataExtractor = _dataExtractor;
    }

    @Override
    public void process(MultipartFile file, String path) throws TikaException, IOException, SAXException {
        dataExtractor.extract(file.getInputStream());
        assetCreator.createAsset(file,path);
    }
}
