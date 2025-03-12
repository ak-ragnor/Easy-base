package com.easybase.core.sync;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import com.easybase.core.collection.EntityDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Implementation of SyncService that uses Elasticsearch for synchronization.
 */
@Service
public class SyncServiceImpl implements SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncServiceImpl.class);

    private static final int BATCH_SIZE = 100;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final int QUEUE_CAPACITY = 10000;

    @Autowired
    private ElasticsearchClient esClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // Queue for batching sync operations
    private final BlockingQueue<SyncOperation> syncQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    // Executor for processing batches
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    // Retry queues
    private final Map<String, BlockingQueue<SyncOperation>> retryQueues = new ConcurrentHashMap<>();

    // Start the background batch processor
    public SyncServiceImpl() {
        // Start the batch processor
        executor.scheduleWithFixedDelay(
                this::processBatches,
                1000, // Initial delay
                1000, // Period
                TimeUnit.MILLISECONDS
        );

        // Start the retry processor
        executor.scheduleWithFixedDelay(
                this::processRetries,
                5000, // Initial delay
                5000, // Period
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Processes batches of sync operations.
     */
    private void processBatches() {
        try {
            List<SyncOperation> batch = new ArrayList<>(BATCH_SIZE);
            int count = syncQueue.drainTo(batch, BATCH_SIZE);

            if (count > 0) {
                logger.debug("Processing batch of {} operations", count);

                // Group by collection
                Map<String, List<SyncOperation>> byCollection = batch.stream()
                        .collect(Collectors.groupingBy(SyncOperation::getCollectionName));

                // Process each collection batch
                for (Map.Entry<String, List<SyncOperation>> entry : byCollection.entrySet()) {
                    processBatch(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            logger.error("Error processing sync batch: {}", e.getMessage(), e);
        }
    }

    /**
     * Processes a batch of sync operations for a specific collection.
     *
     * @param collectionName the collection name
     * @param operations the operations to process
     */
    private void processBatch(String collectionName, List<SyncOperation> operations) {
        try {
            // Skip empty batches
            if (operations.isEmpty()) {
                return;
            }

            // Create bulk request
            BulkRequest.Builder bulkRequest = new BulkRequest.Builder();

            // Add operations to the bulk request
            for (SyncOperation operation : operations) {
                switch (operation.getType()) {
                    case INDEX:
                        bulkRequest.operations(op -> op
                                .index(idx -> idx
                                        .index(collectionName)
                                        .id(operation.getId())
                                        .document(operation.getData())
                                )
                        );
                        break;
                    case DELETE:
                        bulkRequest.operations(op -> op
                                .delete(del -> del
                                        .index(collectionName)
                                        .id(operation.getId())
                                )
                        );
                        break;
                }
            }

            // Execute the bulk request
            BulkResponse response = esClient.bulk(bulkRequest.build());

            // Check for failures
            if (response.errors()) {
                logger.warn("Bulk sync had errors");

                // Process failures
                for (int i = 0; i < response.items().size(); i++) {
                    BulkResponseItem item = response.items().get(i);
                    if (item.error() != null) {
                        SyncOperation failedOp = operations.get(i);
                        logger.warn("Failed to sync {}: {}", failedOp.getId(), item.error().reason());

                        // Add to retry queue
                        addToRetryQueue(failedOp);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error processing sync batch for collection {}: {}", collectionName, e.getMessage(), e);

            // Add all operations to retry queue
            for (SyncOperation operation : operations) {
                addToRetryQueue(operation);
            }
        }
    }

    /**
     * Adds an operation to the retry queue.
     *
     * @param operation the operation to retry
     */
    private void addToRetryQueue(SyncOperation operation) {
        String collectionName = operation.getCollectionName();
        BlockingQueue<SyncOperation> retryQueue = retryQueues.computeIfAbsent(
                collectionName,
                k -> new LinkedBlockingQueue<>()
        );

        // Only retry if under max retries
        if (operation.getRetryCount() < MAX_RETRIES) {
            operation.incrementRetryCount();
            retryQueue.offer(operation);
        } else {
            logger.error("Giving up on syncing {} after {} retries", operation.getId(), MAX_RETRIES);
            // TODO: Add to error log or dead letter queue
        }
    }

    /**
     * Processes retry queues.
     */
    private void processRetries() {
        try {
            for (Map.Entry<String, BlockingQueue<SyncOperation>> entry : retryQueues.entrySet()) {
                String collectionName = entry.getKey();
                BlockingQueue<SyncOperation> retryQueue = entry.getValue();

                List<SyncOperation> retries = new ArrayList<>(BATCH_SIZE);
                int count = retryQueue.drainTo(retries, BATCH_SIZE);

                if (count > 0) {
                    logger.debug("Processing {} retries for collection {}", count, collectionName);
                    processBatch(collectionName, retries);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing retries: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void syncRecord(String collectionName, Map<String, Object> record) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        Objects.requireNonNull(record, "Record cannot be null");

        String id = _extractId(record);
        if (id == null) {
            throw new IllegalArgumentException("Record must have an 'id' field");
        }

        // If we're in a transaction, register a post-commit hook
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    _enqueueSyncOperation(collectionName, id, record, SyncOperation.Type.INDEX);
                }
            });
        } else {
            // No transaction, sync immediately
            _enqueueSyncOperation(collectionName, id, record, SyncOperation.Type.INDEX);
        }
    }

    @Override
    @Transactional
    public void syncRecord(String collectionName, Object record) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        Objects.requireNonNull(record, "Record cannot be null");

        try {
            // Convert object to map
            Map<String, Object> recordMap = objectMapper.convertValue(record, Map.class);
            syncRecord(collectionName, recordMap);
        } catch (Exception e) {
            logger.error("Failed to convert record to map: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to sync record", e);
        }
    }

    @Override
    @Transactional
    public void syncBatch(String collectionName, List<Map<String, Object>> records) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        Objects.requireNonNull(records, "Records cannot be null");

        // If we're in a transaction, register a post-commit hook
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    for (Map<String, Object> record : records) {
                        String id = _extractId(record);
                        if (id != null) {
                            _enqueueSyncOperation(collectionName, id, record, SyncOperation.Type.INDEX);
                        }
                    }
                }
            });
        } else {
            // No transaction, sync immediately
            for (Map<String, Object> record : records) {
                String id = _extractId(record);
                if (id != null) {
                    _enqueueSyncOperation(collectionName, id, record, SyncOperation.Type.INDEX);
                }
            }
        }
    }

    @Override
    @Transactional
    public void syncBatchObjects(String collectionName, List<?> records) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        Objects.requireNonNull(records, "Records cannot be null");

        try {
            // Convert objects to maps
            List<Map<String, Object>> recordMaps = new ArrayList<>(records.size());
            for (Object record : records) {
                Map<String, Object> recordMap = objectMapper.convertValue(record, Map.class);
                recordMaps.add(recordMap);
            }
            syncBatch(collectionName, recordMaps);
        } catch (Exception e) {
            logger.error("Failed to convert records to maps: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to sync batch", e);
        }
    }

    @Override
    @Transactional
    public void deleteRecord(String collectionName, String id) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        Objects.requireNonNull(id, "ID cannot be null");

        // If we're in a transaction, register a post-commit hook
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    _enqueueSyncOperation(collectionName, id, null, SyncOperation.Type.DELETE);
                }
            });
        } else {
            // No transaction, delete immediately
            _enqueueSyncOperation(collectionName, id, null, SyncOperation.Type.DELETE);
        }
    }

    @Override
    @Transactional
    public void deleteBatch(String collectionName, List<String> ids) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        Objects.requireNonNull(ids, "IDs cannot be null");

        // If we're in a transaction, register a post-commit hook
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    for (String id : ids) {
                        _enqueueSyncOperation(collectionName, id, null, SyncOperation.Type.DELETE);
                    }
                }
            });
        } else {
            // No transaction, delete immediately
            for (String id : ids) {
                _enqueueSyncOperation(collectionName, id, null, SyncOperation.Type.DELETE);
            }
        }
    }

    @Override
    public boolean verifyConsistency(String collectionName) {
        try {
            // Count records in database
            String sql = "SELECT COUNT(*) FROM " + collectionName;
            long dbCount = jdbcTemplate.queryForObject(sql, Long.class);

            // Count documents in Elasticsearch
            CountRequest countRequest = CountRequest.of(c -> c.index(collectionName));
            CountResponse countResponse = esClient.count(countRequest);
            long esCount = countResponse.count();

            logger.info("Consistency check for {}: DB={}, ES={}", collectionName, dbCount, esCount);

            return dbCount == esCount;
        } catch (Exception e) {
            logger.error("Error verifying consistency for {}: {}", collectionName, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int repairInconsistencies(String collectionName) {
        int repairedCount = 0;

        try {
            // Get all IDs from database
            String sqlIds = "SELECT id FROM " + collectionName;
            List<String> dbIds = jdbcTemplate.queryForList(sqlIds, String.class);
            Set<String> dbIdSet = new HashSet<>(dbIds);

            // Get all IDs from Elasticsearch
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(collectionName)
                    .size(10000) // This is a limitation - for large collections, use scan/scroll API
                    .source(src -> src.filter(f -> f.includes("id")))
            );

            SearchResponse<Map> searchResponse = esClient.search(searchRequest, Map.class);
            Set<String> esIds = searchResponse.hits().hits().stream()
                    .map(hit -> hit.id())
                    .collect(Collectors.toSet());

            // Find IDs in database but not in Elasticsearch (need to be indexed)
            Set<String> missingInEs = new HashSet<>(dbIdSet);
            missingInEs.removeAll(esIds);

            // Find IDs in Elasticsearch but not in database (need to be deleted)
            Set<String> missingInDb = new HashSet<>(esIds);
            missingInDb.removeAll(dbIdSet);

            // Repair: index missing records
            for (String id : missingInEs) {
                String sql = "SELECT * FROM " + collectionName + " WHERE id = ?";
                Map<String, Object> record = jdbcTemplate.queryForMap(sql, id);
                syncRecord(collectionName, record);
                repairedCount++;
            }

            // Repair: delete orphaned documents
            for (String id : missingInDb) {
                deleteRecord(collectionName, id);
                repairedCount++;
            }

            logger.info("Repaired {} inconsistencies for {}: {} indexed, {} deleted",
                    repairedCount, collectionName, missingInEs.size(), missingInDb.size());

            return repairedCount;
        } catch (Exception e) {
            logger.error("Error repairing inconsistencies for {}: {}", collectionName, e.getMessage(), e);
            return repairedCount;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int reindexCollection(String collectionName) {
        try {
            // Delete the index
            DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(d -> d.index(collectionName));
            esClient.indices().delete(deleteRequest);

            // Create the index (assuming it already exists and mappings are preserved)
            CreateIndexRequest createRequest = CreateIndexRequest.of(c -> c.index(collectionName));
            esClient.indices().create(createRequest);

            // Fetch all records from the database
            String sql = "SELECT * FROM " + collectionName;
            List<Map<String, Object>> records = jdbcTemplate.queryForList(sql);

            // Reindex all records
            syncBatch(collectionName, records);

            logger.info("Reindexed {} records for collection {}", records.size(), collectionName);

            return records.size();
        } catch (Exception e) {
            logger.error("Error reindexing collection {}: {}", collectionName, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Extracts the ID from a record.
     *
     * @param record the record
     * @return the ID, or null if not found
     */
    private String _extractId(Map<String, Object> record) {
        Object id = record.get("id");
        return id != null ? id.toString() : null;
    }

    /**
     * Enqueues a sync operation.
     *
     * @param collectionName the collection name
     * @param id the record ID
     * @param data the record data
     * @param type the operation type
     */
    private void _enqueueSyncOperation(String collectionName, String id, Map<String, Object> data, SyncOperation.Type type) {
        SyncOperation operation = new SyncOperation(collectionName, id, data, type);

        boolean enqueued = syncQueue.offer(operation);
        if (!enqueued) {
            logger.warn("Sync queue full, performing sync immediately for {}", id);
            _performSyncImmediately(operation);
        }
    }

    /**
     * Performs a sync operation immediately, bypassing the queue.
     *
     * @param operation the operation to perform
     */
    private void _performSyncImmediately(SyncOperation operation) {
        try {
            switch (operation.getType()) {
                case INDEX:
                    IndexRequest<Map<String, Object>> indexRequest = IndexRequest.of(r -> r
                            .index(operation.getCollectionName())
                            .id(operation.getId())
                            .document(operation.getData())
                    );
                    IndexResponse indexResponse = esClient.index(indexRequest);

                    if (indexResponse.result() != Result.Created && indexResponse.result() != Result.Updated) {
                        logger.warn("Failed to index document {}: {}", operation.getId(), indexResponse.result());
                    }
                    break;

                case DELETE:
                    DeleteRequest deleteRequest = DeleteRequest.of(r -> r
                            .index(operation.getCollectionName())
                            .id(operation.getId())
                    );
                    DeleteResponse deleteResponse = esClient.delete(deleteRequest);

                    if (deleteResponse.result() != Result.Deleted) {
                        logger.warn("Failed to delete document {}: {}", operation.getId(), deleteResponse.result());
                    }
                    break;
            }
        } catch (IOException e) {
            logger.error("Error performing immediate sync for {}: {}", operation.getId(), e.getMessage(), e);
        }
    }

    /**
     * Scheduled task to check and repair inconsistencies.
     */
    @Scheduled(fixedDelay = 3600000) // 1 hour
    public void scheduledConsistencyCheck() {
        try {
            // Get all collection names
            List<String> collections = _getAllCollections();

            for (String collectionName : collections) {
                boolean consistent = verifyConsistency(collectionName);
                if (!consistent) {
                    logger.info("Scheduled consistency check found inconsistencies in {}, repairing...", collectionName);
                    repairInconsistencies(collectionName);
                }
            }
        } catch (Exception e) {
            logger.error("Error in scheduled consistency check: {}", e.getMessage(), e);
        }
    }

    /**
     * Gets all collection names.
     *
     * @return a list of collection names
     */
    private List<String> _getAllCollections() {
        try {
            return jdbcTemplate.queryForList("SELECT name FROM eb_collection_metadata", String.class);
        } catch (DataAccessException e) {
            logger.error("Error getting collections: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Represents a sync operation.
     */
    private static class SyncOperation {
        enum Type {
            INDEX,
            DELETE
        }

        private final String collectionName;
        private final String id;
        private final Map<String, Object> data;
        private final Type type;
        private int retryCount = 0;

        /**
         * Constructs a new sync operation.
         *
         * @param collectionName the collection name
         * @param id the record ID
         * @param data the record data (null for DELETE operations)
         * @param type the operation type
         */
        public SyncOperation(String collectionName, String id, Map<String, Object> data, Type type) {
            this.collectionName = collectionName;
            this.id = id;
            this.data = data;
            this.type = type;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public String getId() {
            return id;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public Type getType() {
            return type;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void incrementRetryCount() {
            this.retryCount++;
        }
    }
}