/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.validation;

import com.easybase.common.exception.ValidationException;

import com.google.re2j.Pattern;
import com.google.re2j.PatternSyntaxException;

import java.util.Map;

/**
 * @author Akhash R
 */
public class PatternValidator implements Validator {

	@Override
	public void validate(
		String fieldName, Object value, Map<String, Object> config) {

		Object pattern = config.get("pattern");

		if ((pattern == null) || (value == null)) {
			return;
		}

		String str = value.toString();

		try {
			Pattern re2Pattern = Pattern.compile(pattern.toString());

			if (!re2Pattern.matches(str)) {
				throw new ValidationException(
					fieldName, str,
					String.format("must match pattern '%s'", pattern));
			}
		}
		catch (PatternSyntaxException patternSyntaxException) {
			throw new ValidationException(
				fieldName, str,
				String.format(
					"invalid regex pattern '%s'",
					patternSyntaxException.getPattern()));
		}
	}

}