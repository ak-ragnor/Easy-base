/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;
import com.easybase.core.data.engine.domain.type.AttributeTypeDefinition;
import com.easybase.core.data.engine.domain.validation.NumericRangeValidator;
import com.easybase.core.data.engine.domain.validation.RequiredValidator;
import com.easybase.core.data.engine.domain.validation.Validator;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class DecimalTypeDefinition implements AttributeTypeDefinition {

	@Override
	public AttributeType getType() {
		return AttributeType.DECIMAL;
	}

	@Override
	public String resolvePostgresType(Map<String, Object> config) {
		return "numeric";
	}

	@Override
	public void validate(
		String fieldName, Object value, Map<String, Object> config) {

		_validateType(fieldName, value, config);

		for (Validator validator : _validators) {
			validator.validate(fieldName, value, config);
		}
	}

	private BigDecimal _toBigDecimal(String fieldName, Object value) {
		if (value instanceof BigDecimal) {
			return (BigDecimal)value;
		}

		if (value instanceof Integer || value instanceof Long) {
			Number number = (Number)value;

			return BigDecimal.valueOf(number.longValue());
		}

		if (value instanceof Double || value instanceof Float) {
			return new BigDecimal(value.toString());
		}

		try {
			return new BigDecimal(value.toString());
		}
		catch (NumberFormatException numberFormatException) {
			throw new ValidationException(
				String.format(
					"Validation failed for field '%s' with value '%s': %s",
					fieldName, value, "expected a decimal value"),
				numberFormatException);
		}
	}

	private Integer _toInteger(Object value) {
		if (value instanceof Number) {
			Number number = (Number)value;

			return number.intValue();
		}

		return null;
	}

	private void _validateType(
		String fieldName, Object value, Map<String, Object> config) {

		if ((config == null) || (value == null)) {
			return;
		}

		BigDecimal bigDecimal = _toBigDecimal(fieldName, value);

		bigDecimal = bigDecimal.stripTrailingZeros();

		Integer precision = _toInteger(config.get("precision"));

		if ((precision != null) && (bigDecimal.precision() > precision)) {
			throw new ValidationException(
				fieldName, value.toString(),
				String.format("precision must be <= %d", precision));
		}

		Integer scale = _toInteger(config.get("scale"));

		if (scale != null) {
			int actualScale = Math.max(bigDecimal.scale(), 0);

			if (actualScale > scale) {
				throw new ValidationException(
					fieldName, value.toString(),
					String.format("scale must be <= %d", scale));
			}
		}
	}

	private final List<Validator> _validators = List.of(
		new RequiredValidator(), new NumericRangeValidator());

}