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
class PatternValidatorTest {

	@Test
	public void testEmailPattern() {
		_validator.validate(
			"field", "test@example.com",
			Map.of("pattern", "^[\\w.]+@[\\w.]+$"));
	}

	@Test
	public void testNoPatternConfig() {
		_validator.validate("field", "anything", Collections.emptyMap());
	}

	@Test
	public void testPatternDoesNotMatch() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _validator.validate(
				"field", "ABC", Map.of("pattern", "^[a-z]+$")));
	}

	@Test
	public void testPatternMatches() {
		_validator.validate("field", "abc", Map.of("pattern", "^[a-z]+$"));
	}

	private final PatternValidator _validator = new PatternValidator();

}