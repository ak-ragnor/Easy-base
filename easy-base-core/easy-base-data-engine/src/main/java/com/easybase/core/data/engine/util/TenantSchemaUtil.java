package com.easybase.core.data.engine.util;

import java.util.UUID;

import org.jooq.Name;
import org.jooq.impl.DSL;

public class TenantSchemaUtil {
	private TenantSchemaUtil() {
	}

	public static Name tableName(UUID tenantId, String collectionName) {
		String tableName = NamingUtils.generateTableName(tenantId,
				collectionName);

		return DSL.name(getSchema(tenantId), tableName);
	}

	public static String getSchema(UUID tenantId) {
		return "tenant_" + tenantId.toString().replace("-", "_").toLowerCase();
	}
}
