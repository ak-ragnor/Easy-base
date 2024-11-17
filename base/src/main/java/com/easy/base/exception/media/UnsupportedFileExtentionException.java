package com.easy.base.exception.media;

public class UnsupportedFileExtentionException extends RuntimeException{
    public UnsupportedFileExtentionException(String extension){
        super(extension+" is not supported");
    }
}
