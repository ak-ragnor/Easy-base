package com.easybase.core.data.engine.service.ddl;

import static org.jooq.impl.DSL.*;

import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TableManager {

	public void createTableIfNotExists(String schema, String table) {
		_dslContext.createTableIfNotExists(name(schema, table))
				.column("id",
						SQLDataType.UUID.nullable(false).defaultValue(
								function("gen_random_uuid", SQLDataType.UUID)))
				.column("created_at",
						SQLDataType.TIMESTAMP.nullable(false)
								.defaultValue(currentTimestamp()))
				.column("updated_at",
						SQLDataType.TIMESTAMP.nullable(false)
								.defaultValue(currentTimestamp()))
				.column("data", SQLDataType.JSONB).constraint(primaryKey("id"))
				.execute();

		_triggerManager.createUpdatedAtTrigger(schema, table);
	}

	public void dropTableIfExists(String schema, String table) {
		_dslContext.dropTableIfExists(name(schema, table)).cascade().execute();
	}

	private final DSLContext _dslContext;

	private final TriggerManager _triggerManager;
}
