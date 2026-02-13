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

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class BinaryTypeDefinition implements AttributeTypeDefinition {

	@Override
	public AttributeType getType() {
		return AttributeType.BINARY;
	}

	@Override
	public String resolvePostgresType(Map<String, Object> config) {
		return "bytea";
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
		if (value instanceof byte[]) {
			return;
		}

		try {
			Base64.Decoder decoder = Base64.getDecoder();

			decoder.decode(value.toString());
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new ValidationException(
				String.format(
					"Validation failed for field '%s' with value '%s': %s",
					fieldName, value.toString(),
					"expected a Base64-encoded binary value"),
				illegalArgumentException);
		}
	}

	private final List<Validator> _validators = List.of(
		new RequiredValidator());

}