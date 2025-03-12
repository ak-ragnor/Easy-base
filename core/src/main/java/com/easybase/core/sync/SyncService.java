package com.easybase.core.sync;

import java.util.List;
import java.util.Map;

/**
 * Service for synchronizing data between the SQL database and Elasticsearch.
 */
public interface SyncService {

    /**
     * Synchronizes a record to Elasticsearch.
     *
     * @param collectionName the collection name
     * @param record the record to synchronize
     */
    void syncRecord(String collectionName, Map<String, Object> record);

    /**
     * Synchronizes a record to Elasticsearch.
     *
     * @param collectionName the collection name
     * @param record the record to synchronize
     */
    void syncRecord(String collectionName, Object record);

    /**
     * Synchronizes a batch of records to Elasticsearch.
     *
     * @param collectionName the collection name
     * @param records the records to synchronize
     */
    void syncBatch(String collectionName, List<Map<String, Object>> records);

    /**
     * Synchronizes a batch of records to Elasticsearch.
     *
     * @param collectionName the collection name
     * @param records the records to synchronize
     */
    void syncBatchObjects(String collectionName, List<?> records);

    /**
     * Deletes a record from Elasticsearch.
     *
     * @param collectionName the collection name
     * @param id the record ID
     */
    void deleteRecord(String collectionName, String id);

    /**
     * Deletes a batch of records from Elasticsearch.
     *
     * @param collectionName the collection name
     * @param ids the record IDs
     */
    void deleteBatch(String collectionName, List<String> ids);

    /**
     * Verifies consistency between the database and Elasticsearch.
     *
     * @param collectionName the collection name
     * @return true if consistent, false otherwise
     */
    boolean verifyConsistency(String collectionName);

    /**
     * Repairs inconsistencies between the database and Elasticsearch.
     *
     * @param collectionName the collection name
     * @return the number of records repaired
     */
    int repairInconsistencies(String collectionName);

    /**
     * Performs a full reindex of a collection.
     *
     * @param collectionName the collection name
     * @return the number of records reindexed
     */
    int reindexCollection(String collectionName);
}