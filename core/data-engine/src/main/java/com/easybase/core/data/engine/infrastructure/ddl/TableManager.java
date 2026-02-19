/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.infrastructure.ddl;

import com.easybase.common.util.ListUtil;

import java.util.List;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class TableManager {

	public void addSearchVector(String table, List<String> textAttributeNames) {
		if (ListUtil.isEmpty(textAttributeNames)) {
			return;
		}

		_dslContext.execute(
			"ALTER TABLE {0} ADD COLUMN IF NOT EXISTS search_vector tsvector",
			DSL.table(DSL.name(table)));

		_triggerManager.createSearchVectorTrigger(table, textAttributeNames);

		_indexManager.createSearchVectorGinIndex(table);

		_dslContext.execute(
			"UPDATE {0} SET search_vector = search_vector",
			DSL.table(DSL.name(table)));

		log.info(
			"Added FTS search_vector to table {} for attributes: {}", table,
			textAttributeNames);
	}

	public void createTableIfNotExists(String table) {
		var tableBuilder = _dslContext.createTableIfNotExists(DSL.name(table));

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

		_triggerManager.createUpdatedAtTrigger(table);
	}

	public void dropTableIfExists(String table) {
		var dropBuilder = _dslContext.dropTableIfExists(DSL.name(table));

		dropBuilder = (DropTableStep)dropBuilder.cascade();

		dropBuilder.execute();
	}

	private final DSLContext _dslContext;
	private final IndexManager _indexManager;
	private final TriggerManager _triggerManager;

}