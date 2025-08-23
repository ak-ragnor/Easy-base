package com.easybase.core.data.engine.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;

import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.data.engine.entity.DataRecord;
import com.easybase.core.data.engine.util.TenantSchemaUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Repository for dynamic record operations
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class DataRecordRepository {

	private final DSLContext dsl;

	public DataRecord insert(UUID tenantId, String table, UUID id,
			Object data) {
		return dsl
				.insertInto(
						DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
				.set(DSL.field("id"), id)
				.set(DSL.field("data"), DSL.val(data, SQLDataType.JSONB))
				.returning(DSL.field("id"), DSL.field("data"),
						DSL.field("created_at"), DSL.field("updated_at"))
				.fetchOne(this::toDataRecord);
	}

	public Optional<DataRecord> findById(UUID tenantId, String table, UUID id) {
		return dsl.select()
				.from(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
				.where(DSL.field("id").eq(id))
				.fetchOptional(this::toDataRecord);
	}

	public List<DataRecord> findAll(UUID tenantId, String table) {
		return dsl.select()
				.from(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
				.orderBy(DSL.field("created_at").desc())
				.fetch(this::toDataRecord);
	}

	public DataRecord update(UUID tenantId, String table, UUID id,
			Map<String, Object> data) {

		int affected = dsl
				.update(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
				.set(DSL.field("data"), DSL.val(data, SQLDataType.JSONB))
				.set(DSL.field("updated_at"), DSL.currentTimestamp())
				.where(DSL.field("id").eq(id)).execute();

		if (affected == 0) {
			throw new ResourceNotFoundException("Record", "id", id);
		}

		return findById(tenantId, table, id)
				.orElseThrow(() -> new IllegalStateException(
						"Record not found after update: " + id));
	}

	public void delete(UUID tenantId, String table, UUID id) {
		dsl.deleteFrom(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
				.where(DSL.field("id").eq(id)).execute();
	}

	public boolean exists(UUID tenantId, String table, UUID id) {
		return dsl.fetchExists(dsl.select()
				.from(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
				.where(DSL.field("id").eq(id)));
	}

	private DataRecord toDataRecord(Record record) {
		if (record == null) {
			return null;
		}

		UUID id = record.get("id", UUID.class);

		Timestamp createdTs = record.get("created_at", Timestamp.class);
		Timestamp updatedTs = record.get("updated_at", Timestamp.class);

		LocalDateTime createdAt = createdTs != null
				? createdTs.toLocalDateTime()
				: null;
		LocalDateTime updatedAt = updatedTs != null
				? updatedTs.toLocalDateTime()
				: null;

		String json = record.get("data", String.class);
		Map<String, Object> data = null;
		if (json != null) {
			try {
				data = objectMapper.readValue(json,
						new TypeReference<Map<String, Object>>() {
						});
			} catch (Exception e) {
				throw new RuntimeException(
						"Failed to parse JSONB column 'data'", e);
			}
		}

		return new DataRecord(id, data, createdAt, updatedAt);
	}

	private final ObjectMapper objectMapper = new ObjectMapper();
}
