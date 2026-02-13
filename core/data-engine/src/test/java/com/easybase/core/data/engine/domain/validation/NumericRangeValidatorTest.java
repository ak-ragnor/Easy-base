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
class NumericRangeValidatorTest {

	@Test
	void testNoRangeConfig() {
		_validator.validate("field", 50, Collections.emptyMap());
	}

	@Test
	void testMinPasses() {
		_validator.validate("field", 10, Map.of("min", 10));
	}

	@Test
	void testMinFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _validator.validate("field", 5, Map.of("min", 10)));
	}

	@Test
	void testMaxPasses() {
		_validator.validate("field", 100, Map.of("max", 100));
	}

	@Test
	void testMaxFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _validator.validate("field", 200, Map.of("max", 100)));
	}

	@Test
	void testMinAndMaxPasses() {
		_validator.validate("field", 50, Map.of("min", 10, "max", 100));
	}

	@Test
	void testMinAndMaxFailsBelowMin() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _validator.validate(
				"field", 5, Map.of("min", 10, "max", 100)));
	}

	@Test
	void testMinAndMaxFailsAboveMax() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _validator.validate(
				"field", 200, Map.of("min", 10, "max", 100)));
	}

	@Test
	void testDecimalRange() {
		_validator.validate("field", 5.5, Map.of("min", 1.0, "max", 10.0));
	}

	private final NumericRangeValidator _validator =
		new NumericRangeValidator();

}