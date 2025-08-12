package com.easybase.core.dataengine.service.ddl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Component;

import static org.jooq.impl.DSL.*;

@Component
@RequiredArgsConstructor
public class TableManager {

    private final DSLContext dsl;
    private final TriggerManager triggerManager;

    public void createTableIfNotExists(String schema, String table) {
        dsl.createTableIfNotExists(name(schema, table))
                .column("id", SQLDataType.UUID.nullable(false).defaultValue(function("gen_random_uuid", SQLDataType.UUID)))
                .column("created_at", SQLDataType.TIMESTAMP.nullable(false).defaultValue(currentTimestamp()))
                .column("updated_at", SQLDataType.TIMESTAMP.nullable(false).defaultValue(currentTimestamp()))
                .column("data", SQLDataType.JSONB)
                .constraint(primaryKey("id"))
                .execute();

        triggerManager.createUpdatedAtTrigger(schema, table);
    }

    public void dropTableIfExists(String schema, String table) {
        dsl.dropTableIfExists(name(schema, table)).cascade().execute();
    }
}
