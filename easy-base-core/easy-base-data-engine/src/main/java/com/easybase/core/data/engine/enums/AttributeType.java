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