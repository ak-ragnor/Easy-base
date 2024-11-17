package com.easy.base.exception.media;

public class ExceededFileNumberException extends RuntimeException{
    public ExceededFileNumberException(int acceptedNo){
        super(acceptedNo +" is the maximum number for bulk upload");
    }
}
