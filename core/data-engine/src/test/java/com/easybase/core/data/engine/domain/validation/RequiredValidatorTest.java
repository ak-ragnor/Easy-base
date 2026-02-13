/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.validation;

import com.easybase.common.exception.ValidationException;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Akhash R
 */
class RequiredValidatorTest {

	@Test
	public void testNotNullValueWithRequiredTrue() {
		_validator.validate("field", "value", Map.of("required", Boolean.TRUE));
	}

	@Test
	public void testNullValueWithoutRequiredConfig() {
		_validator.validate("field", null, Collections.emptyMap());
	}

	@Test
	public void testNullValueWithRequiredFalse() {
		_validator.validate("field", null, Map.of("required", Boolean.FALSE));
	}

	@Test
	public void testNullValueWithRequiredTrue() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _validator.validate(
				"field", null, Map.of("required", Boolean.TRUE)));
	}

	private final RequiredValidator _validator = new RequiredValidator();

}