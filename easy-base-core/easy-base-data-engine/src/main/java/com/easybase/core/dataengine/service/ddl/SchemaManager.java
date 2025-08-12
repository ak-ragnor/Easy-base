package com.easybase.core.dataengine.service.ddl;

import com.easybase.core.dataengine.util.TenantSchemaUtil;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SchemaManager {

    private final DSLContext dsl;

    public void createSchemaIfNotExists(UUID tenantId) {
        String schema = TenantSchemaUtil.getSchema(tenantId);

        dsl.createSchemaIfNotExists(DSL.name(schema)).execute();
    }

    public void dropSchemaIfExists(UUID tenantId) {
        String schema = TenantSchemaUtil.getSchema(tenantId);

        dsl.dropSchemaIfExists(DSL.name(schema)).cascade().execute();
    }
}
