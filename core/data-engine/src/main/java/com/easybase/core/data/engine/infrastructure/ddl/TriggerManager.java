/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.infrastructure.ddl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TriggerManager {

	public void createSearchVectorTrigger(
		String table, List<String> textAttributeNames) {

		String functionName = table + "_search_vector_update";

		String quotedFunctionName = "\"" + functionName + "\"";

		Stream<String> nameStream = textAttributeNames.stream();

		String functionSql = String.format(
			"CREATE OR REPLACE FUNCTION %s() RETURNS TRIGGER AS $$ BEGIN NEW.search_vector := %s; RETURN NEW; END; $$ LANGUAGE 'plpgsql';",
			quotedFunctionName,
			nameStream.map(
				attr ->
					"to_tsvector('simple', coalesce(NEW.data->>'" + attr +
						"',''))"
			).collect(
				Collectors.joining(" || ")
			));

		_dslContext.execute(functionSql);

		String triggerName = "trg_" + table + "_search_vector";

		_dslContext.execute(
			"DROP TRIGGER IF EXISTS {0} ON {1}", DSL.name(triggerName),
			DSL.table(DSL.name(table)));

		_dslContext.execute(
			"CREATE TRIGGER {0} BEFORE INSERT OR UPDATE ON {1} FOR EACH ROW EXECUTE FUNCTION " +
				quotedFunctionName + "()",
			DSL.name(triggerName), DSL.table(DSL.name(table)));

		log.debug(
			"Created search vector trigger {} on table {}", triggerName, table);
	}

	public void createUpdatedAtTrigger(String table) {
		String functionSql =
			"CREATE OR REPLACE FUNCTION update_updated_at_column() RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = now(); RETURN NEW; END; $$ LANGUAGE 'plpgsql';";

		_dslContext.execute(functionSql);

		String triggerName = "update_" + table + "_updated_at";

		_dslContext.execute(
			"DROP TRIGGER IF EXISTS {0} ON {1}", DSL.name(triggerName),
			DSL.table(DSL.name(table)));

		_dslContext.execute(
			"CREATE TRIGGER {0} BEFORE UPDATE ON {1} FOR EACH ROW EXECUTE FUNCTION update_updated_at_column()",
			DSL.name(triggerName), DSL.table(DSL.name(table)));
	}

	private final DSLContext _dslContext;

}