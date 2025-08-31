/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.util;

import java.util.UUID;

import org.jooq.Name;
import org.jooq.impl.DSL;

/**
 * @author Akhash R
 */
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