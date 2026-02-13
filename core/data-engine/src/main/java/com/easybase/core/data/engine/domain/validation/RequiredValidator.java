/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.validation;

import com.easybase.common.exception.ValidationException;

import java.util.Map;

/**
 * @author Akhash R
 */
public class RequiredValidator implements Validator {

	@Override
	public void validate(
		String fieldName, Object value, Map<String, Object> config) {

		if (!Boolean.TRUE.equals(config.get("required"))) {
			return;
		}

		if (value == null) {
			throw new ValidationException(
				fieldName, "null", "field is required");
		}
	}

}