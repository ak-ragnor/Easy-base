package com.easybase.core.data.engine.service.ddl;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import com.easybase.core.data.engine.enums.AttributeType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IndexManager {

	private final DSLContext dsl;

	public void createGinIndexIfNotExists(String schema, String table) {
		String indexName = table + "_data_gin_idx";

		dsl.execute("CREATE INDEX IF NOT EXISTS {0} ON {1} USING GIN (data)",
				DSL.name(indexName), DSL.table(DSL.name(schema, table)));
	}

	public void createAttributeIndexIfNotExists(String schema, String table,
			String attributeName, AttributeType type) {
		String indexName = String.format("%s_%s_idx", table, attributeName);
		String castType = type.getPostgresType();

		dsl.execute(
				"CREATE INDEX IF NOT EXISTS {0} ON {1}.{2} (((data->>'{3}')::{4}))",
				DSL.name(indexName), DSL.name(schema), DSL.name(table),
				DSL.inline(attributeName), DSL.keyword(castType));
	}

	public void dropAttributeIndexIfExists(String schema, String table,
			String attributeName) {
		String indexName = String.format("%s_%s_idx", table, attributeName);

		dsl.execute("DROP INDEX IF EXISTS {0}.{1}", DSL.name(schema),
				DSL.name(indexName));
	}
}
