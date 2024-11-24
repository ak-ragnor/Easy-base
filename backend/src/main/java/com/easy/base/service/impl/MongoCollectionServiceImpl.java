package com.easy.base.service.impl;

import com.easy.base.flexi.builder.SchemaBuilder;
import com.easy.base.model.FlexiTableField;
import com.easy.base.service.MongoCollectionService;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MongoCollectionServiceImpl implements MongoCollectionService {
    private final MongoTemplate mongoTemplate;
    private final SchemaBuilder schemaBuilder;

    public MongoCollectionServiceImpl(MongoTemplate mongoTemplate, SchemaBuilder schemaBuilder) {
        this.mongoTemplate = mongoTemplate;
        this.schemaBuilder = schemaBuilder;
    }

    @Override
    public void createCollection(String collectionName, List<FlexiTableField> fields) {
        Document schemaDocument = schemaBuilder.withFields(fields).build();
        mongoTemplate.createCollection(collectionName);

        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        collection.updateOne(new Document("name", collectionName), new Document("$set", new Document("validator", schemaDocument)));

        _indexFields(collection, fields);
    }

    private void _indexFields(MongoCollection<Document> collection, List<FlexiTableField> fields) {
        List<Document> indexes = fields.stream()
                .filter(FlexiTableField::isIndexed)
                .map(field -> new Document(field.getFieldName(), 1))
                .toList();

        if (!indexes.isEmpty()) {
            for (Document index : indexes) {
                collection.createIndex(index);
            }
        }
    }

}
