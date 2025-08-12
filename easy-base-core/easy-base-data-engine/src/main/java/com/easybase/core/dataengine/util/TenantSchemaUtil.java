package com.easybase.core.dataengine.util;

import org.jooq.Name;
import org.jooq.impl.DSL;

import java.util.UUID;

public class TenantSchemaUtil {
    private TenantSchemaUtil() {}

    public static Name tableName(UUID tenantId, String tableName) {
        return DSL.name(getSchema(tenantId), tableName);
    }

    public static String getSchema(UUID tenantId) {
        return "tenant_" + tenantId.toString().replace("-", "_").toLowerCase();
    }
}
