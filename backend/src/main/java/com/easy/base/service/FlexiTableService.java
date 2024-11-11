package com.easy.base.service;

import com.easy.base.model.FlexiTable;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

public interface FlexiTableService{

    public Optional<FlexiTable> getFlexiTablesByWorkspaceId(String workspaceId);
    public FlexiTable getFlexiTableById(String id);
    public FlexiTable updateFlexiTable(FlexiTable flexiTable);
    public void deleteFlexiTable(String id);
    public List<Document> getAllRecords(String workspaceId, String tableName);
    public Document getRecordById(String workspaceId, String tableName, String id);
    public Document createRecord(String workspaceId, String tableName, Document data);
    public Document updateRecord(String workspaceId, String tableName, String id, Document data);
    public void deleteRecord(String workspaceId, String tableName, String id);
    public FlexiTable createFlexiTable(FlexiTable flexiTable);
}
