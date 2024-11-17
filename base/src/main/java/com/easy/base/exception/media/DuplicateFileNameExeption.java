package com.easy.base.exception.media;

public class DuplicateFileNameExeption extends RuntimeException{
    public DuplicateFileNameExeption(String name, String parentId){
        super(name+" already exist in folderId "+parentId);
    }
}
