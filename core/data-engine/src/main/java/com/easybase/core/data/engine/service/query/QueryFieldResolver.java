/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.query;

import com.easybase.common.exception.InvalidRequestException;
import com.easybase.core.data.engine.domain.entity.Attribute;
import com.easybase.core.data.engine.domain.entity.Collection;
import com.easybase.core.data.engine.domain.enums.AttributeType;
import com.easybase.core.data.engine.domain.type.AttributeTypeDefinition;
import com.easybase.core.data.engine.domain.type.AttributeTypeDefinitionRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class QueryFieldResolver {

	public static final Map<String, String> systemColumns;

	static {
		Map<String, String> columns = new HashMap<>();

		columns.put("createdAt", "timestamp");
		columns.put("id", "uuid");
		columns.put("updatedAt", "timestamp");

		systemColumns = Collections.unmodifiableMap(columns);
	}

	public AttributeType getAttributeType(
		Collection collection, String fieldName) {

		if (systemColumns.containsKey(fieldName)) {
			return null;
		}

		Attribute attribute = _findAttribute(collection, fieldName);

		return attribute.getDataType();
	}

	/**
	 * Checks if a field is a system column (id, createdAt, updatedAt)
	 * rather than a JSONB data field.
	 */
	public boolean isSystemColumn(String fieldName) {
		return systemColumns.containsKey(fieldName);
	}

	/**
	 * Resolves the PostgreSQL type for a given field in a collection.
	 *
	 * @param collection the collection with loaded attributes
	 * @param fieldName the field name to resolve
	 * @return the PostgreSQL type string (e.g. "text", "integer", "timestamp")
	 * @throws InvalidRequestException if the field does not exist
	 */
	public String resolvePostgresType(Collection collection, String fieldName) {
		if (systemColumns.containsKey(fieldName)) {
			return systemColumns.get(fieldName);
		}

		Attribute attribute = _findAttribute(collection, fieldName);

		AttributeTypeDefinition typeDefinition =
			_attributeTypeDefinitionRegistry.getDescriptor(
				attribute.getDataType());

		Map<String, Object> config = attribute.getConfig();

		if (config == null) {
			config = Collections.emptyMap();
		}

		return typeDefinition.resolvePostgresType(config);
	}

	public String toDbColumnName(String fieldName) {
		switch (fieldName) {
			case "createdAt":
				return "created_at";
			case "updatedAt":
				return "updated_at";
			default:
				return fieldName;
		}
	}

	public void validateFields(Collection collection, Set<String> fieldNames) {
		for (String fieldName : fieldNames) {
			if (!systemColumns.containsKey(fieldName)) {
				_findAttribute(collection, fieldName);
			}
		}
	}

	private Attribute _findAttribute(Collection collection, String fieldName) {
		if (collection.getAttributes() != null) {
			for (Attribute attribute : collection.getAttributes()) {
				String attributeName = attribute.getName();

				if (attributeName.equals(fieldName)) {
					return attribute;
				}
			}
		}

		throw new InvalidRequestException(
			"Unknown field '" + fieldName + "' in collection '" +
				collection.getName() + "'");
	}

	private final AttributeTypeDefinitionRegistry
		_attributeTypeDefinitionRegistry;

}