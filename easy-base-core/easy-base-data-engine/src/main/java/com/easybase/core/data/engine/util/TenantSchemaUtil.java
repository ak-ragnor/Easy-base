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

package com.easybase.core.data.engine.util;

import java.util.UUID;

import org.jooq.Name;
import org.jooq.impl.DSL;

public class TenantSchemaUtil {

	public static String getSchema(UUID tenantId) {
		String tenantIdString = tenantId.toString();

		String replaced = tenantIdString.replace("-", "_");

		String schema = replaced.toLowerCase();

		return "tenant_" + schema;
	}

	public static Name tableName(UUID tenantId, String collectionName) {
		String tableName = NamingUtils.generateTableName(
			tenantId, collectionName);

		return DSL.name(getSchema(tenantId), tableName);
	}

	private TenantSchemaUtil() {
	}

}