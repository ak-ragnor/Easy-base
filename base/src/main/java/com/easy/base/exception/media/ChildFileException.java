package com.easy.base.exception.media;

public class ChildFileException extends RuntimeException {
    public ChildFileException(String type, String parentId){
        super("No "+type+"s exist under the parent "+parentId);
    }
}
