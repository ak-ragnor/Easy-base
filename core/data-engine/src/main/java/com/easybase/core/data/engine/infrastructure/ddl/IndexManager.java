/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.infrastructure.ddl;

import lombok.RequiredArgsConstructor;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class IndexManager {

	public void createAttributeIndexIfNotExists(
		String table, String attributeName, String postgresType) {

		String indexName = String.format("%s_%s_idx", table, attributeName);

		_dslContext.execute(
			"CREATE INDEX IF NOT EXISTS {0} ON {1} (((data->>'{2}')::{3}))",
			DSL.name(indexName), DSL.name(table), DSL.inline(attributeName),
			DSL.keyword(postgresType));
	}

	public void createGinIndexIfNotExists(String table) {
		String indexName = table + "_data_gin_idx";

		_dslContext.execute(
			"CREATE INDEX IF NOT EXISTS {0} ON {1} USING GIN (data)",
			DSL.name(indexName), DSL.table(DSL.name(table)));
	}

	public void dropAttributeIndexIfExists(String table, String attributeName) {
		String indexName = String.format("%s_%s_idx", table, attributeName);

		_dslContext.execute("DROP INDEX IF EXISTS {0}", DSL.name(indexName));
	}

	private final DSLContext _dslContext;

}