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
class StringLengthValidatorTest {

	@Test
	public void testMaxLengthFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _validator.validate(
				"field", "abcdef", Map.of("maxLength", 5)));
	}

	@Test
	public void testMaxLengthPasses() {
		_validator.validate("field", "abcde", Map.of("maxLength", 5));
	}

	@Test
	public void testMinAndMaxLengthFailsBelowMin() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _validator.validate(
				"field", "a", Map.of("minLength", 2, "maxLength", 5)));
	}

	@Test
	public void testMinAndMaxLengthPasses() {
		_validator.validate(
			"field", "abcd", Map.of("minLength", 2, "maxLength", 5));
	}

	@Test
	public void testMinLengthFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _validator.validate("field", "ab", Map.of("minLength", 3)));
	}

	@Test
	public void testMinLengthPasses() {
		_validator.validate("field", "abc", Map.of("minLength", 3));
	}

	@Test
	public void testNoLengthConfig() {
		_validator.validate("field", "anything", Collections.emptyMap());
	}

	private final StringLengthValidator _validator =
		new StringLengthValidator();

}