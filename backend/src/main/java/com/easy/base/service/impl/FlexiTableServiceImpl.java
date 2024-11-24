package com.easy.base.service.impl;

import com.easy.base.flexi.factory.ValidationFactory;
import com.easy.base.flexi.selector.ValidationFactorySelector;
import com.easy.base.model.FlexiFieldValidation;
import com.easy.base.model.FlexiTableField;
import com.easy.base.service.MongoCollectionService;
import com.easy.base.model.FlexiTable;
import com.easy.base.repository.FlexiTableRepository;
import com.easy.base.service.FlexiTableService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FlexiTableServiceImpl implements FlexiTableService {

    private final FlexiTableRepository flexiTableRepository;
    private final MongoTemplate mongoTemplate;
    private final MongoCollectionService mongoCollectionService;
    private final ValidationFactorySelector validationFactorySelector;

    public FlexiTableServiceImpl(FlexiTableRepository flexiTableRepository,
                                 MongoTemplate mongoTemplate,
                                 MongoCollectionService mongoCollectionService,
                                 ValidationFactorySelector validationFactorySelector) {
        this.flexiTableRepository = flexiTableRepository;
        this.mongoTemplate = mongoTemplate;
        this.mongoCollectionService = mongoCollectionService;
        this.validationFactorySelector = validationFactorySelector;
    }

    /**
     * Retrieves a FlexiTable by workspace ID and table name.
     *
     * @param workspaceId the ID of the workspace.
     * @param tableName the name of the table.
     * @return FlexiTable if found, or throws IllegalArgumentException.
     */
    private FlexiTable _getTableOrThrow(String workspaceId, String tableName) {
        return flexiTableRepository.findByNameAndWorkspaceId(tableName, new ObjectId(workspaceId))
                .orElseThrow(() -> new IllegalArgumentException("Table not found with workspaceId: " + workspaceId + " and tableName: " + tableName));
    }

    /**
     * Retrieves a FlexiTable by its workspace ID.
     *
     * @param workspaceId the ID of the workspace.
     * @return an Optional containing the FlexiTable if found.
     */
    public Optional<FlexiTable> getFlexiTablesByWorkspaceId(String workspaceId) {
        return flexiTableRepository.findById(workspaceId);
    }

    /**
     * Retrieves a FlexiTable by its ID.
     *
     * @param id the ID of the FlexiTable.
     * @return the FlexiTable if found, or throws IllegalArgumentException.
     */
    public FlexiTable getFlexiTableById(String id) {
        return flexiTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FlexiTable not found with id: " + id));
    }

    /**
     * Deletes a FlexiTable by its ID.
     *
     * @param id the ID of the FlexiTable.
     */
    public void deleteFlexiTable(String id) {
        if (!flexiTableRepository.existsById(id)) {
            throw new IllegalArgumentException("FlexiTable not found with id: " + id);
        }
        flexiTableRepository.deleteById(id);
    }

    /**
     * Retrieves all records from a specified table within a workspace.
     *
     * @param workspaceId the ID of the workspace.
     * @param tableName the name of the table.
     * @return a list of documents containing the table records.
     */
    public List<Document> getAllRecords(String workspaceId, String tableName) {
        String dynamicTableName = _getTableOrThrow(workspaceId, tableName).getFlexiName();
        return mongoTemplate.findAll(Document.class, dynamicTableName);
    }

    /**
     * Retrieves a single record by ID from a specified table within a workspace.
     *
     * @param workspaceId the ID of the workspace.
     * @param tableName the name of the table.
     * @param id the ID of the record.
     * @return the document containing the record data.
     */
    public Document getRecordById(String workspaceId, String tableName, String id) {
        String dynamicTableName = _getTableOrThrow(workspaceId, tableName).getFlexiName();
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        return mongoTemplate.findOne(query, Document.class, dynamicTableName);
    }

    /**
     * Creates a new record in a specified table within a workspace.
     *
     * @param workspaceId the ID of the workspace.
     * @param tableName the name of the table.
     * @param data the data to be inserted.
     * @return the document containing the created record.
     */

    //have to fix it should be based on flexi table name
    public Document createRecord(String workspaceId, String tableName, Document data) {
        FlexiTable table = _getTableOrThrow(workspaceId, tableName);
        String flexiName = table.getFlexiName();
        List<FlexiTableField> flexiTableFields = table.getFields();

        for (FlexiTableField field : flexiTableFields) {
            String fieldName = field.getFieldName();
            Object value = data.get(fieldName);

            if (field.isRequired() && value == null) {
                throw new IllegalArgumentException(fieldName + " is required");
            }

            if (value != null) {
                ValidationFactory factory = validationFactorySelector.getFactory(field);
                _validateField(factory, field, value);
            }
        }

        return mongoTemplate.insert(data, flexiName);
    }

    /**
     * Updates an existing record in a specified table within a workspace.
     *
     * @param workspaceId the ID of the workspace.
     * @param tableName the name of the table.
     * @param id the ID of the record.
     * @param data the updated data.
     * @return the document containing the updated record.
     */
    public Document updateRecord(String workspaceId, String tableName, String id, Document data) {
        String dynamicTableName = _getTableOrThrow(workspaceId, tableName).getFlexiName();
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update();
        data.forEach(update::set);
        mongoTemplate.updateFirst(query, update, dynamicTableName);
        return data;
    }

    /**
     * Deletes a record by ID from a specified table within a workspace.
     *
     * @param workspaceId the ID of the workspace.
     * @param tableName the name of the table.
     * @param id the ID of the record to delete.
     */
    public void deleteRecord(String workspaceId, String tableName, String id) {
        String dynamicTableName = _getTableOrThrow(workspaceId, tableName).getFlexiName();
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        mongoTemplate.remove(query, dynamicTableName);
    }

    /**
     * Creates a new FlexiTable along with its collection.
     *
     * @param flexiTable the FlexiTable to create.
     * @return the created FlexiTable.
     */
    @Transactional(rollbackFor = Exception.class)
    public FlexiTable createFlexiTable(FlexiTable flexiTable) {

        if (flexiTableRepository.existsByNameAndWorkspaceId(flexiTable.getName(), flexiTable.getWorkspaceId())) {
            throw new IllegalArgumentException("Choice list with this name " + flexiTable.getName() + " already exists.");
        }

        flexiTable.setCreatedDate(new Date());
        flexiTable.setModifiedDate(new Date());

        mongoCollectionService.createCollection(flexiTable.getCollectionName(), flexiTable.getFields());
        return flexiTableRepository.save(flexiTable);
    }

    /**
     * Updates an existing FlexiTable.
     *
     * @param flexiTable the FlexiTable to update.
     * @return the updated FlexiTable.
     */
    public FlexiTable updateFlexiTable(FlexiTable flexiTable) {
        return flexiTableRepository.save(flexiTable);
    }

    private void _validateField(ValidationFactory factory, FlexiTableField field, Object value) {
        for (FlexiFieldValidation rule : field.getValidations()) {
            if (!factory.validate(field, value, rule)) {
                throw new IllegalArgumentException("Validation failed for field: " + field.getFieldName()
                        + " with rule: " + rule);
            }
        }
    }
}
