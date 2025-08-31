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