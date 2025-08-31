/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.enums;

import lombok.Getter;

/**
 * Supported attribute types for dynamic collections
 */
@Getter
public enum AttributeType {

	BIGINT("bigint"), BOOLEAN("boolean"), BYTEA("bytea"), CHAR("char"),
	DATE("date"), DATETIME("timestamp"), DECIMAL("decimal"),
	DOUBLE("double precision"), INTEGER("integer"), JSON("json"),
	JSONB("jsonb"), NUMERIC("numeric"), REAL("real"), TEXT("text"),
	TIME("time"), TIMESTAMP("timestamp"), UUID("uuid"), VARCHAR("varchar");

	private final String postgresType;

	AttributeType(String postgresType) {
		this.postgresType = postgresType;
	}

}