package com.easybase.core.data.engine.enums;

import lombok.Getter;

/**
 * Supported attribute types for dynamic collections
 */
@Getter
public enum AttributeType {

	TEXT("text"), VARCHAR("varchar"), CHAR("char"),

	INTEGER("integer"), BIGINT("bigint"), DECIMAL("decimal"), NUMERIC(
			"numeric"), REAL("real"), DOUBLE("double precision"),

	BOOLEAN("boolean"),

	DATE("date"), TIME("time"), TIMESTAMP("timestamp"), DATETIME("timestamp"),

	UUID("uuid"),

	JSON("json"), JSONB("jsonb"),

	BYTEA("bytea");

	private final String postgresType;

	AttributeType(String postgresType) {
		this.postgresType = postgresType;
	}

}
