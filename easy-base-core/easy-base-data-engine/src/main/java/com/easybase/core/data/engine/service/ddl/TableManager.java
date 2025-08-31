/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.core.data.engine.service.ddl;

import lombok.RequiredArgsConstructor;

import org.jooq.DSLContext;
import org.jooq.DropTableStep;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import org.springframework.stereotype.Component;

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