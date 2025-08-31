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