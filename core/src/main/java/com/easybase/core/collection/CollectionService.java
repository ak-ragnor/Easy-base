package com.easybase.core.collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing dynamic collections in the system.
 * This service provides functionality for creating and managing schema-less collections,
 * similar to document databases, but with the backing of SQL and Elasticsearch.
 */
public interface CollectionService {

    /**
     * Creates a new collection with the given name and field definitions.
     *
     * @param collectionName the name of the collection to create
     * @param fieldDefinitions the field definitions for the collection
     * @return the created EntityDefinition
     */
    EntityDefinition createCollection(String collectionName, List<FieldDefinition> fieldDefinitions);

    /**
     * Updates an existing collection with new field definitions.
     *
     * @param collectionName the name of the collection to update
     * @param fieldDefinitions the new field definitions
     * @return the updated EntityDefinition
     */
    EntityDefinition updateCollection(String collectionName, List<FieldDefinition> fieldDefinitions);

    /**
     * Deletes a collection by name.
     *
     * @param collectionName the name of the collection to delete
     * @return true if deleted, false otherwise
     */
    boolean deleteCollection(String collectionName);

    /**
     * Gets a collection by name.
     *
     * @param collectionName the name of the collection to retrieve
     * @return the collection, if found
     */
    Optional<EntityDefinition> getCollection(String collectionName);

    /**
     * Lists all collections.
     *
     * @return a list of all collections
     */
    List<EntityDefinition> listCollections();

    /**
     * Creates a record in a collection.
     *
     * @param collectionName the collection name
     * @param recordData the record data as a map
     * @return the created record
     */
    Map<String, Object> createRecord(String collectionName, Map<String, Object> recordData);

    /**
     * Updates an existing record in a collection.
     *
     * @param collectionName the collection name
     * @param id the record ID
     * @param recordData the updated record data
     * @return the updated record
     */
    Map<String, Object> updateRecord(String collectionName, String id, Map<String, Object> recordData);

    /**
     * Gets a record by ID from a collection.
     *
     * @param collectionName the collection name
     * @param id the record ID
     * @return the record, if found
     */
    Optional<Map<String, Object>> getRecord(String collectionName, String id);

    /**
     * Deletes a record from a collection.
     *
     * @param collectionName the collection name
     * @param id the record ID
     * @return true if deleted, false otherwise
     */
    boolean deleteRecord(String collectionName, String id);

    /**
     * Searches a collection with optional query, filter, and sort parameters.
     *
     * @param collectionName the collection name
     * @param search the search query (optional)
     * @param filter the filter expression (optional)
     * @param sort the sort expression (optional)
     * @param pageable pagination information
     * @return a page of matching records
     */
    Page<Map<String, Object>> search(String collectionName, String search, String filter, String sort, Pageable pageable);

    /**
     * Registers an entity definition with the collection service.
     *
     * @param definition the entity definition to register
     */
    void registerEntityDefinition(EntityDefinition definition);
}