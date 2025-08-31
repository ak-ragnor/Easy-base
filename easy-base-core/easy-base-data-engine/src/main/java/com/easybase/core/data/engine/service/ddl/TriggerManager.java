/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.ddl;

import lombok.RequiredArgsConstructor;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TriggerManager {

	public void createUpdatedAtTrigger(String schema, String table) {
		String functionSql =
			"CREATE OR REPLACE FUNCTION update_updated_at_column() RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = now(); RETURN NEW; END; $$ LANGUAGE 'plpgsql';";

		_dslContext.execute(functionSql);

		String triggerName = "update_" + table + "_updated_at";

		_dslContext.execute(
			"DROP TRIGGER IF EXISTS {0} ON {1}", DSL.name(triggerName),
			DSL.table(DSL.name(schema, table)));

		_dslContext.execute(
			"CREATE TRIGGER {0} BEFORE UPDATE ON {1} FOR EACH ROW EXECUTE FUNCTION update_updated_at_column()",
			DSL.name(triggerName), DSL.table(DSL.name(schema, table)));
	}

	private final DSLContext _dslContext;

}