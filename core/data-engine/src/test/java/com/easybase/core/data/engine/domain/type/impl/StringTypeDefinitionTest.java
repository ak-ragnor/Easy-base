/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Akhash R
 */
class StringTypeDefinitionTest {

	@Test
	void testGetType() {
		Assertions.assertEquals(AttributeType.STRING, _definition.getType());
	}

	@Test
	void testResolvePostgresTypeNoConfig() {
		Assertions.assertEquals(
			"text", _definition.resolvePostgresType(Collections.emptyMap()));
	}

	@Test
	void testResolvePostgresTypeNullConfig() {
		Assertions.assertEquals("text", _definition.resolvePostgresType(null));
	}

	@Test
	void testResolvePostgresTypeWithMaxLength() {
		Assertions.assertEquals(
			"varchar",
			_definition.resolvePostgresType(Map.of("maxLength", 255)));
	}

	@Test
	void testValidateString() {
		_definition.validate("field", "hello", Collections.emptyMap());
	}

	@Test
	void testValidateEmptyString() {
		_definition.validate("field", "", Collections.emptyMap());
	}

	@Test
	void testValidateNonStringFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate("field", 123, Collections.emptyMap()));
	}

	@Test
	void testValidateMinLengthPasses() {
		_definition.validate("field", "abc", Map.of("minLength", 3));
	}

	@Test
	void testValidateMinLengthFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate("field", "ab", Map.of("minLength", 3)));
	}

	@Test
	void testValidateMaxLengthPasses() {
		_definition.validate("field", "abc", Map.of("maxLength", 5));
	}

	@Test
	void testValidateMaxLengthFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", "abcdef", Map.of("maxLength", 5)));
	}

	@Test
	void testValidatePatternPasses() {
		_definition.validate("field", "abc", Map.of("pattern", "^[a-z]+$"));
	}

	@Test
	void testValidatePatternFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", "ABC", Map.of("pattern", "^[a-z]+$")));
	}

	@Test
	void testValidateRequiredWithValue() {
		_definition.validate(
			"field", "hello", Map.of("required", Boolean.TRUE));
	}

	private final StringTypeDefinition _definition = new StringTypeDefinition();

}