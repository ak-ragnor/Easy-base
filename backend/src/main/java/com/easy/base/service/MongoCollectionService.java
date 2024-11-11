package com.easy.base.service;

import com.easy.base.model.FlexiTableField;

import java.util.List;

public interface MongoCollectionService {

    void createCollection(String collectionName, List<FlexiTableField> fields);
}
