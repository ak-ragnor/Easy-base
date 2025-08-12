package com.easybase.core.dataengine.repository;

import com.easybase.core.dataengine.util.TenantSchemaUtil;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RecordRepository {

    private final DSLContext dsl;

    public void insert(UUID tenantId, String table, UUID id, Map<String, Object> data) {
        dsl.insertInto(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
                .set(DSL.field("id"), id)
                .set(DSL.field("data"), DSL.val(data, SQLDataType.JSONB))
                .execute();
    }

    public Optional<Map<String, Object>> findById(UUID tenantId, String table, UUID id) {
        Record record = dsl.select()
                .from(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
                .where(DSL.field("id").eq(id))
                .fetchOne();

        return Optional.ofNullable(record).map(Record::intoMap);
    }

    public List<Map<String, Object>> findAll(UUID tenantId, String table) {
        return dsl.select()
                .from(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
                .fetch(Record::intoMap);
    }

    public void update(UUID tenantId, String table, UUID id, Map<String, Object> data) {
        dsl.update(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
                .set(DSL.field("data"), DSL.val(data, SQLDataType.JSONB))
                .where(DSL.field("id").eq(id))
                .execute();
    }

    public void patchField(UUID tenantId, String table, UUID id, String jsonPath, Object value) {
        dsl.update(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
                .set(DSL.field("data"),
                        DSL.function("jsonb_set", SQLDataType.JSONB,
                                DSL.field("data"),
                                DSL.val("{" + jsonPath + "}"),
                                DSL.val(value, SQLDataType.JSONB),
                                DSL.inline(true)
                        )
                )
                .where(DSL.field("id").eq(id))
                .execute();
    }

    public void delete(UUID tenantId, String table, UUID id) {
        dsl.deleteFrom(DSL.table(TenantSchemaUtil.tableName(tenantId, table)))
                .where(DSL.field("id").eq(id))
                .execute();
    }
}