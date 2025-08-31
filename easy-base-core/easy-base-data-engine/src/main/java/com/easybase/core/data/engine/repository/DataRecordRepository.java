/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.repository;

import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.data.engine.entity.DataRecord;
import com.easybase.core.data.engine.util.TenantSchemaUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.DeleteUsingStep;
import org.jooq.Field;
import org.jooq.InsertResultStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectWhereStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.UpdateConditionStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.UpdateSetStep;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import org.springframework.stereotype.Repository;

/**
 * Repository for dynamic record operations
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class DataRecordRepository {

	public void delete(UUID tenantId, String table, UUID id) {
		Table<?> dynamicTable = DSL.table(
			TenantSchemaUtil.tableName(tenantId, table));

		DeleteUsingStep<?> deleteUsingStep = _dslContext.deleteFrom(
			dynamicTable);

		Field<Object> idField = DSL.field("id");

		Condition condition = idField.eq(id);

		DeleteConditionStep<?> deleteConditionStep = deleteUsingStep.where(
			condition);

		deleteConditionStep.execute();
	}

	public boolean exists(UUID tenantId, String table, UUID id) {
		Table<?> dynamicTable = DSL.table(
			TenantSchemaUtil.tableName(tenantId, table));

		SelectWhereStep<?> selectFromStep = _dslContext.selectFrom(
			dynamicTable);

		Field<Object> idField = DSL.field("id");

		Condition condition = idField.eq(id);

		SelectConditionStep<?> selectConditionStep = selectFromStep.where(
			condition);

		return _dslContext.fetchExists(selectConditionStep);
	}

	public List<DataRecord> findAll(UUID tenantId, String table) {
		Table<?> dynamicTable = DSL.table(
			TenantSchemaUtil.tableName(tenantId, table));

		SelectWhereStep<?> selectFromStep = _dslContext.selectFrom(
			dynamicTable);

		Field<Object> createdAtField = DSL.field("created_at");

		SortField<?> sortField = createdAtField.desc();

		SelectLimitStep<?> selectLimitStep = selectFromStep.orderBy(sortField);

		return selectLimitStep.fetch(this::_toDataRecord);
	}

	public Optional<DataRecord> findById(UUID tenantId, String table, UUID id) {
		Table<?> dynamicTable = DSL.table(
			TenantSchemaUtil.tableName(tenantId, table));

		SelectWhereStep<?> selectFromStep = _dslContext.selectFrom(
			dynamicTable);

		Field<Object> idField = DSL.field("id");

		Condition condition = idField.eq(id);

		SelectConditionStep<?> selectConditionStep = selectFromStep.where(
			condition);

		return selectConditionStep.fetchOptional(this::_toDataRecord);
	}

	public DataRecord insert(
		UUID tenantId, String table, UUID id, Object data) {

		Table<?> dynamicTable = DSL.table(
			TenantSchemaUtil.tableName(tenantId, table));

		InsertSetStep<?> insertSetStep = _dslContext.insertInto(dynamicTable);

		Field<Object> idField = DSL.field("id");

		InsertSetMoreStep<?> insertSetMoreStep = insertSetStep.set(idField, id);

		Field<Object> dataField = DSL.field("data");

		InsertSetMoreStep<?> insertValuesStep = insertSetMoreStep.set(
			dataField, DSL.val(data, SQLDataType.JSONB));

		Field<Object> createdAtField = DSL.field("created_at");
		Field<Object> updatedAtField = DSL.field("updated_at");

		InsertResultStep<?> insertResultStep = insertValuesStep.returning(
			idField, dataField, createdAtField, updatedAtField);

		return insertResultStep.fetchOne(this::_toDataRecord);
	}

	public DataRecord update(
		UUID tenantId, String table, UUID id, Map<String, Object> data) {

		Table<?> dynamicTable = DSL.table(
			TenantSchemaUtil.tableName(tenantId, table));

		UpdateSetStep<?> updateSetStep = _dslContext.update(dynamicTable);

		Field<Object> dataField = DSL.field("data");

		UpdateSetMoreStep<?> updateWithData = updateSetStep.set(
			dataField, DSL.val(data, SQLDataType.JSONB));

		Field<Object> updatedAtField = DSL.field("updated_at");

		UpdateSetMoreStep<?> updateWithTimestamp = updateWithData.set(
			updatedAtField, DSL.currentTimestamp());

		Field<Object> idField = DSL.field("id");

		Condition condition = idField.eq(id);

		UpdateConditionStep<?> updateWhereStep = updateWithTimestamp.where(
			condition);

		int affected = updateWhereStep.execute();

		if (affected == 0) {
			throw new ResourceNotFoundException("Record", "id", id);
		}

		return findById(
			tenantId, table, id
		).orElseThrow(
			() -> new IllegalStateException(
				"Record not found after update: " + id)
		);
	}

	private DataRecord _toDataRecord(Record record) {
		if (record == null) {
			return null;
		}

		UUID id = record.get("id", UUID.class);

		Timestamp createdTs = record.get("created_at", Timestamp.class);
		Timestamp updatedTs = record.get("updated_at", Timestamp.class);

		LocalDateTime createdAt = null;

		if (createdTs != null) {
			createdAt = createdTs.toLocalDateTime();
		}

		LocalDateTime updatedAt = null;

		if (updatedTs != null) {
			updatedAt = updatedTs.toLocalDateTime();
		}

		String json = record.get("data", String.class);
		Map<String, Object> data = null;

		if (json != null) {
			try {
				data = _objectMapper.readValue(
					json,
					new TypeReference<Map<String, Object>>() {
					});
			}
			catch (Exception exception) {
				throw new RuntimeException(
					"Failed to parse JSONB column 'data'", exception);
			}
		}

		return new DataRecord(createdAt, data, id, updatedAt);
	}

	private final DSLContext _dslContext;
	private final ObjectMapper _objectMapper = new ObjectMapper();

}