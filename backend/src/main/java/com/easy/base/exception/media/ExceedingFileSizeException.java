package com.easy.base.exception.media;

public class ExceedingFileSizeException extends RuntimeException{
    public ExceedingFileSizeException(String fileName, String fileSize, String acceptedFileSize){
        super("File "+fileName+" having size "+fileSize+" exceeded the accepted size " +acceptedFileSize);
    }

}
