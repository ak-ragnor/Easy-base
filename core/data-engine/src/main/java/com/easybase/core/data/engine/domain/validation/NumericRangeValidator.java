/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.validation;

import com.easybase.common.exception.ValidationException;

import java.math.BigDecimal;

import java.util.Map;

/**
 * @author Akhash R
 */
public class NumericRangeValidator implements Validator {

	@Override
	public void validate(
		String fieldName, Object value, Map<String, Object> config) {

		BigDecimal numericValue = new BigDecimal(value.toString());

		BigDecimal min = _toBigDecimal(config.get("min"));

		if ((min != null) && (numericValue.compareTo(min) < 0)) {
			throw new ValidationException(
				fieldName, value.toString(),
				String.format("must be >= %s", min));
		}

		BigDecimal max = _toBigDecimal(config.get("max"));

		if ((max != null) && (numericValue.compareTo(max) > 0)) {
			throw new ValidationException(
				fieldName, value.toString(),
				String.format("must be <= %s", max));
		}
	}

	private BigDecimal _toBigDecimal(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return new BigDecimal(value.toString());
		}

		return new BigDecimal(value.toString());
	}

}