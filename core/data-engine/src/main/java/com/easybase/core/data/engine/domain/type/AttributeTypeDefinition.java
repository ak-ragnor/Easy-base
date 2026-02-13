/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type;

import com.easybase.core.data.engine.domain.enums.AttributeType;

import java.util.Map;

/**
 * @author Akhash R
 */
public interface AttributeTypeDefinition {

	public AttributeType getType();

	public String resolvePostgresType(Map<String, Object> config);

	public void validate(
		String fieldName, Object value, Map<String, Object> config);

}