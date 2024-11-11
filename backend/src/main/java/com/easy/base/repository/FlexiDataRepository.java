package com.easy.base.repository;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FlexiDataRepository {
    Document save(String collectionName, Document document);
    List<Document> saveAll(String collectionName, List<Document> documents);
    List<Document> findAll(String collectionName, Bson query);
    Document findOne(String collectionName, String id);
    long count(String collectionName);
    long delete(String collectionName, String id);
    long deleteAll(String collectionName);
    Document update(String collectionName, Document document);

}
