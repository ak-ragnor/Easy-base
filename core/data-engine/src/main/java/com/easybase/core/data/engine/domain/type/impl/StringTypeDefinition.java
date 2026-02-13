/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;
import com.easybase.core.data.engine.domain.type.AttributeTypeDefinition;
import com.easybase.core.data.engine.domain.validation.PatternValidator;
import com.easybase.core.data.engine.domain.validation.RequiredValidator;
import com.easybase.core.data.engine.domain.validation.StringLengthValidator;
import com.easybase.core.data.engine.domain.validation.Validator;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class StringTypeDefinition implements AttributeTypeDefinition {

	@Override
	public AttributeType getType() {
		return AttributeType.STRING;
	}

	@Override
	public String resolvePostgresType(Map<String, Object> config) {
		if (config == null) {
			return "text";
		}

		Object maxLength = config.get("maxLength");

		if (maxLength != null) {
			return "varchar";
		}

		return "text";
	}

	@Override
	public void validate(
		String fieldName, Object value, Map<String, Object> config) {

		_validateType(fieldName, value);

		for (Validator validator : _validators) {
			validator.validate(fieldName, value, config);
		}
	}

	private void _validateType(String fieldName, Object value) {
		if (value instanceof String) {
			return;
		}

		throw new ValidationException(
			fieldName, String.valueOf(value), "expected a string value");
	}

	private final List<Validator> _validators = List.of(
		new RequiredValidator(), new StringLengthValidator(),
		new PatternValidator());

}