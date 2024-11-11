package com.easy.base.service.impl;

import com.easy.base.service.MongoCollectionService;
import com.easy.base.factory.builder.SchemaBuilder;
import com.easy.base.model.FlexiTableField;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MongoCollectionServiceImpl implements MongoCollectionService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SchemaBuilder schemaBuilder;

    @Override
    public void createCollection(String collectionName, List<FlexiTableField> fields) {

        Document schemaDocument = schemaBuilder.withFields(fields).build();

        mongoTemplate.createCollection(collectionName);
        mongoTemplate.getDb().runCommand(new Document("collMod", collectionName).append("validator", schemaDocument));

        _indexFields(collectionName, fields);
    }

    //need more research
    private void _indexFields(String collectionName, List<FlexiTableField> fields) {
        fields.stream()
                .filter(FlexiTableField::isIndexed)
                .forEach(field -> mongoTemplate.getCollection(collectionName)
                        .createIndex(new Document(field.getFieldName(), 1)));
    }

}
