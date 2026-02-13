/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;
import com.easybase.core.data.engine.domain.type.AttributeTypeDefinition;
import com.easybase.core.data.engine.domain.validation.RequiredValidator;
import com.easybase.core.data.engine.domain.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class JsonTypeDefinition implements AttributeTypeDefinition {

	@Override
	public AttributeType getType() {
		return AttributeType.JSON;
	}

	@Override
	public String resolvePostgresType(Map<String, Object> config) {
		return "jsonb";
	}

	@Override
	public void validate(
		String fieldName, Object value, Map<String, Object> config) {

		_validateType(fieldName, value);

		_requiredValidator.validate(fieldName, value, config);
	}

	private void _validateType(String fieldName, Object value) {
		if (value instanceof Map) {
			return;
		}

		if (value instanceof List) {
			return;
		}

		try {
			_objectMapper.readTree(value.toString());
		}
		catch (Exception exception) {
			throw new ValidationException(
				fieldName, value.toString(), "expected valid JSON");
		}
	}

	private final ObjectMapper _objectMapper;
	private final Validator _requiredValidator = new RequiredValidator();

}