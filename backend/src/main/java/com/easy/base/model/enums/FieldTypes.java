package com.easy.base.model.enums;

public enum FieldTypes {
    TEXT("string"),
    NUMBER("int"),
    DATE("date"),
    BOOLEAN("bool"),
    CHOICE("array");

    private final String bsonType;

    FieldTypes(String bsonType) {
        this.bsonType = bsonType;
    }

    public String getBsonType() {
        return bsonType;
    }
}
