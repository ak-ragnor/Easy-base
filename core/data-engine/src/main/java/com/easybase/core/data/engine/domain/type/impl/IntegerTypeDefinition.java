/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;
import com.easybase.core.data.engine.domain.type.AttributeTypeDefinition;
import com.easybase.core.data.engine.domain.validation.NumericRangeValidator;
import com.easybase.core.data.engine.domain.validation.RequiredValidator;
import com.easybase.core.data.engine.domain.validation.Validator;

import java.math.BigInteger;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class IntegerTypeDefinition implements AttributeTypeDefinition {

	@Override
	public AttributeType getType() {
		return AttributeType.INTEGER;
	}

	@Override
	public String resolvePostgresType(Map<String, Object> config) {
		Long min = _getLong(config, "min");
		Long max = _getLong(config, "max");

		if (_fitsInInteger(min, max)) {
			return "integer";
		}

		return "bigint";
	}

	@Override
	public void validate(
		String fieldName, Object value, Map<String, Object> config) {

		_validateType(fieldName, value);

		for (Validator validator : _validators) {
			validator.validate(fieldName, value, config);
		}
	}

	private boolean _fitsInInteger(Long min, Long max) {
		if ((min == null) && (max == null)) {
			return true;
		}

		if ((min != null) && (min < _INT_MIN)) {
			return false;
		}

		if ((max != null) && (max > _INT_MAX)) {
			return false;
		}

		return true;
	}

	private Long _getLong(Map<String, Object> config, String key) {
		if (config == null) {
			return null;
		}

		Object value = config.get(key);

		if (value instanceof Number) {
			return ((Number)value).longValue();
		}

		return null;
	}

	private void _validateType(String fieldName, Object value) {
		if (value == null) {
			return;
		}

		if (value instanceof Byte || value instanceof Short ||
			value instanceof Integer || value instanceof Long ||
			value instanceof BigInteger) {

			return;
		}

		if (value instanceof Number) {
			double doubleValue = ((Number)value).doubleValue();

			if ((doubleValue % 1) == 0) {
				return;
			}

			throw new ValidationException(
				fieldName, value.toString(),
				"expected an integer value (no decimals allowed)");
		}

		if (value instanceof String) {
			try {
				new BigInteger((String)value);

				return;
			}
			catch (NumberFormatException ignored) {
			}
		}

		throw new ValidationException(
			fieldName, value.toString(), "expected an integer value");
	}

	private static final long _INT_MAX = Integer.MAX_VALUE;

	private static final long _INT_MIN = Integer.MIN_VALUE;

	private final List<Validator> _validators = List.of(
		new RequiredValidator(), new NumericRangeValidator());

}