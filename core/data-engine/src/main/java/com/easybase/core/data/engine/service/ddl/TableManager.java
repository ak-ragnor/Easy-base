/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.ddl;

import lombok.RequiredArgsConstructor;

import org.jooq.DSLContext;
import org.jooq.DropTableStep;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class TableManager {

	public void createTableIfNotExists(String schema, String table) {
		var tableBuilder = _dslContext.createTableIfNotExists(
			DSL.name(schema, table));

		var idColumn = SQLDataType.UUID;
		idColumn = idColumn.nullable(false);
		idColumn = idColumn.defaultValue(
			DSL.function("gen_random_uuid", SQLDataType.UUID));

		var createdAtColumn = SQLDataType.TIMESTAMP;
		createdAtColumn = createdAtColumn.nullable(false);
		createdAtColumn = createdAtColumn.defaultValue(DSL.currentTimestamp());

		var updatedAtColumn = SQLDataType.TIMESTAMP;
		updatedAtColumn = updatedAtColumn.nullable(false);
		updatedAtColumn = updatedAtColumn.defaultValue(DSL.currentTimestamp());

		tableBuilder = tableBuilder.column("id", idColumn);
		tableBuilder = tableBuilder.column("created_at", createdAtColumn);
		tableBuilder = tableBuilder.column("updated_at", updatedAtColumn);
		tableBuilder = tableBuilder.column("data", SQLDataType.JSONB);
		tableBuilder = tableBuilder.constraint(DSL.primaryKey("id"));

		tableBuilder.execute();

		_triggerManager.createUpdatedAtTrigger(schema, table);
	}

	public void dropTableIfExists(String schema, String table) {
		var dropBuilder = _dslContext.dropTableIfExists(
			DSL.name(schema, table));

		dropBuilder = (DropTableStep)dropBuilder.cascade();

		dropBuilder.execute();
	}

	private final DSLContext _dslContext;
	private final TriggerManager _triggerManager;

}