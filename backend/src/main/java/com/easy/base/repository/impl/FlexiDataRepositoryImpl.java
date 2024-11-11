package com.easy.base.repository.impl;

import com.easy.base.repository.FlexiDataRepository;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class FlexiDataRepositoryImpl implements FlexiDataRepository {

    private static final TransactionOptions txnOptions = TransactionOptions.builder()
            .readPreference(ReadPreference.primary())
            .readConcern(ReadConcern.MAJORITY)
            .writeConcern(WriteConcern.MAJORITY)
            .build();
    private final MongoClient mongoClient;

    public FlexiDataRepositoryImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    private MongoCollection<Document> _getCollection(String collectionName) {
        return mongoClient.getDatabase("easybase").getCollection(collectionName, Document.class);
    }

    @Override
    public Document save(String collectionName, Document document) {
        document.put("_id", new ObjectId());
        _getCollection(collectionName).insertOne(document);
        return document;
    }

    @Override
    public List<Document> saveAll(String collectionName, List<Document> documents) {
        try (ClientSession clientSession = mongoClient.startSession()) {
            return clientSession.withTransaction(() -> {
                documents.forEach(d -> d.put("_id", new ObjectId()));
                _getCollection(collectionName).insertMany(clientSession, documents);
                return documents;
            }, txnOptions);
        }
    }

    @Override
    public List<Document> findAll(String collectionName, Bson query) {
        return _getCollection(collectionName)
                .find(query)
                .into(new ArrayList<>());
    }


    @Override
    public Document findOne(String collectionName, String id) {
        return _getCollection(collectionName).find(eq("_id", new ObjectId(id))).first();
    }

    @Override
    public long count(String collectionName) {
        return _getCollection(collectionName).countDocuments();
    }

    @Override
    public long delete(String collectionName, String id) {
        return _getCollection(collectionName).deleteOne(eq("_id", new ObjectId(id))).getDeletedCount();
    }

    @Override
    public long deleteAll(String collectionName) {
        try (ClientSession clientSession = mongoClient.startSession()) {
            return clientSession.withTransaction(
                    () -> _getCollection(collectionName).deleteMany(new Document()).getDeletedCount(),
                    txnOptions);
        }
    }

    @Override
    public Document update(String collectionName, Document document) {
        return _getCollection(collectionName)
                .findOneAndReplace(eq("_id", document.getObjectId("_id")), document);
    }
    
}
