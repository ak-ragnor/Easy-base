package com.easy.base.exception.media;

public class InvalidFileIdException extends RuntimeException{
    public InvalidFileIdException(String type, String id){
        super("No"+type+ "Exist with id "+id);
    }
    public InvalidFileIdException(String e){
        super(e);
    }
}
