/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.ddl;

import com.easybase.core.data.engine.enums.AttributeType;

import lombok.RequiredArgsConstructor;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndexManager {

	public void createAttributeIndexIfNotExists(
		String schema, String table, String attributeName, AttributeType type) {

		String indexName = String.format("%s_%s_idx", table, attributeName);
		String castType = type.getPostgresType();

		_dslContext.execute(
			"CREATE INDEX IF NOT EXISTS {0} ON {1}.{2} (((data->>'{3}')::{4}))",
			DSL.name(indexName), DSL.name(schema), DSL.name(table),
			DSL.inline(attributeName), DSL.keyword(castType));
	}

	public void createGinIndexIfNotExists(String schema, String table) {
		String indexName = table + "_data_gin_idx";

		_dslContext.execute(
			"CREATE INDEX IF NOT EXISTS {0} ON {1} USING GIN (data)",
			DSL.name(indexName), DSL.table(DSL.name(schema, table)));
	}

	public void dropAttributeIndexIfExists(
		String schema, String table, String attributeName) {

		String indexName = String.format("%s_%s_idx", table, attributeName);

		_dslContext.execute(
			"DROP INDEX IF EXISTS {0}.{1}", DSL.name(schema),
			DSL.name(indexName));
	}

	private final DSLContext _dslContext;

}