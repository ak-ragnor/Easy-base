package com.easy.base.controller;

import com.easy.base.controller.dto.FlexiTableDTO;
import com.easy.base.service.FlexiTableService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
public class FlexiTableController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlexiTableController.class);
    private final FlexiTableService flexiTableService;

    @Autowired
    public FlexiTableController(FlexiTableService flexiTableService) {
        this.flexiTableService = flexiTableService;
    }

    /**
     * Create a new FlexiTable for a workspace.
     *
     * @param workspaceId ID of the workspace
     * @param flexiTable  FlexiTable details
     * @return Created FlexiTable with HTTP 201 status
     */
    @PostMapping("/{workspaceId}/tables")
    public ResponseEntity<FlexiTableDTO> createTable(
            @PathVariable String workspaceId,
            @RequestBody FlexiTableDTO flexiTable) {

            FlexiTableDTO createdTable = new FlexiTableDTO(flexiTableService.createFlexiTable(flexiTable.toNewFlexiTable(workspaceId)));
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTable);

    }

    /**
     * Update a specific FlexiTable by ID.
     *
     * @param workspaceId ID of the workspace
     * @param flexiTable Updated FlexiTable details
     * @return Updated FlexiTable
     */
    @PutMapping("/{workspaceId}/tables")
    public ResponseEntity<FlexiTableDTO> updateTable(
            @PathVariable String workspaceId,
            @RequestBody FlexiTableDTO flexiTable) {

        FlexiTableDTO updatedTable = new FlexiTableDTO(flexiTableService.updateFlexiTable(flexiTable.toFlexiTable(workspaceId)));
        return ResponseEntity.ok(updatedTable);

    }

    /**
     * Get all tables for a specific workspace.
     *
     * @param workspaceId Workspace ID
     * @return List of FlexiTables in the workspace
     */
    @GetMapping("/{workspaceId}/tables")
    public ResponseEntity<List<FlexiTableDTO>> getTablesByWorkspaceId(@PathVariable String workspaceId) {

            List<FlexiTableDTO> tables = flexiTableService.getFlexiTablesByWorkspaceId(workspaceId).stream().map(FlexiTableDTO::new).toList();
            return ResponseEntity.ok(tables);
    }

    /**
     * Retrieve a specific FlexiTable by ID.
     *
     * @param tableId FlexiTable ID
     * @return FlexiTable details
     */
    @GetMapping("/tables/{tableId}")
    public ResponseEntity<FlexiTableDTO> getTableById(@PathVariable String tableId) {

            FlexiTableDTO table = new FlexiTableDTO(flexiTableService.getFlexiTableById(tableId));
            return ResponseEntity.ok(table);
    }

    /**
     * Delete a specific FlexiTable by ID.
     *
     * @param tableId FlexiTable ID
     * @return HTTP status 204 for successful deletion
     */
    @DeleteMapping("/tables/{tableId}")
    public ResponseEntity<Void> deleteTable(@PathVariable String tableId) {

            flexiTableService.deleteFlexiTable(tableId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Retrieve all records in a specific table.
     *
     * @param workspaceId Workspace ID
     * @param tableName   Table name
     * @return List of records as documents
     */
    @GetMapping("/{workspaceId}/tables/{tableName}/records")
    public ResponseEntity<List<Document>> getAllRecords(
            @PathVariable String workspaceId,
            @PathVariable String tableName) {

            List<Document> records = flexiTableService.getAllRecords(workspaceId, tableName);
            return ResponseEntity.ok(records);
    }

    /**
     * Get a specific record by ID from a table.
     *
     * @param workspaceId Workspace ID
     * @param tableName   Table name
     * @param recordId    Record ID
     * @return Document of the specific record
     */
    @GetMapping("/{workspaceId}/tables/{tableName}/records/{recordId}")
    public ResponseEntity<Document> getRecordById(
            @PathVariable String workspaceId,
            @PathVariable String tableName,
            @PathVariable String recordId) {

            Document record = flexiTableService.getRecordById(workspaceId, tableName, recordId);
            return ResponseEntity.ok(record);
    }

    /**
     * Add a new record to a table.
     *
     * @param workspaceId Workspace ID
     * @param tableName   Table name
     * @param data        Data to add
     * @return Document of the created record
     */
    @PostMapping("/{workspaceId}/tables/{tableName}/records")
    public ResponseEntity<Document> createRecord(
            @PathVariable String workspaceId,
            @PathVariable String tableName,
            @RequestBody Document data) {

            Document record = flexiTableService.createRecord(workspaceId, tableName, data);
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    /**
     * Update a record in a table.
     *
     * @param workspaceId Workspace ID
     * @param tableName   Table name
     * @param recordId    Record ID
     * @param data        Updated data
     * @return Document of the updated record
     */
    @PutMapping("/{workspaceId}/tables/{tableName}/records/{recordId}")
    public ResponseEntity<Document> updateRecord(
            @PathVariable String workspaceId,
            @PathVariable String tableName,
            @PathVariable String recordId,
            @RequestBody Document data) {

            Document record = flexiTableService.updateRecord(workspaceId, tableName, recordId, data);
            return ResponseEntity.ok(record);
    }

    /**
     * Delete a specific record by ID from a table.
     *
     * @param workspaceId Workspace ID
     * @param tableName   Table name
     * @param recordId    Record ID
     * @return HTTP status 204 for successful deletion
     */
    @DeleteMapping("/{workspaceId}/tables/{tableName}/records/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable String workspaceId,
            @PathVariable String tableName,
            @PathVariable String recordId) {

            flexiTableService.deleteRecord(workspaceId, tableName, recordId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e) {
        LOGGER.error("Internal server error.", e);
        return e;
    }
}
