package com.easybase.core.data.engine.service.ddl;

import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import com.easybase.core.data.engine.util.TenantSchemaUtil;

import lombok.RequiredArgsConstructor;

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
