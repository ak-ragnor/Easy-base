/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.validation;

import com.easybase.common.exception.ValidationException;

import java.util.Map;

/**
 * @author Akhash R
 */
public class StringLengthValidator implements Validator {

	@Override
	public void validate(
		String fieldName, Object value, Map<String, Object> config) {

		if (value == null) {
			return;
		}

		String str = value.toString();

		Integer minLength = _toInteger(config.get("minLength"));

		if ((minLength != null) && (str.length() < minLength)) {
			throw new ValidationException(
				fieldName, str,
				String.format("length must be >= %d", minLength));
		}

		Integer maxLength = _toInteger(config.get("maxLength"));

		if ((maxLength != null) && (str.length() > maxLength)) {
			throw new ValidationException(
				fieldName, str,
				String.format("length must be <= %d", maxLength));
		}
	}

	private Integer _toInteger(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			Number number = (Number)value;

			return number.intValue();
		}

		return Integer.valueOf(value.toString());
	}

}