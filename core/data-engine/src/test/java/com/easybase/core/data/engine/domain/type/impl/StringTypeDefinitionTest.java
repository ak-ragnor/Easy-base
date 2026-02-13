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
	public void testGetType() {
		Assertions.assertEquals(AttributeType.STRING, _definition.getType());
	}

	@Test
	public void testResolvePostgresTypeNoConfig() {
		Assertions.assertEquals(
			"text", _definition.resolvePostgresType(Collections.emptyMap()));
	}

	@Test
	public void testResolvePostgresTypeNullConfig() {
		Assertions.assertEquals("text", _definition.resolvePostgresType(null));
	}

	@Test
	public void testResolvePostgresTypeWithMaxLength() {
		Assertions.assertEquals(
			"varchar",
			_definition.resolvePostgresType(Map.of("maxLength", 255)));
	}

	@Test
	public void testValidateEmptyString() {
		_definition.validate("field", "", Collections.emptyMap());
	}

	@Test
	public void testValidateMaxLengthFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", "abcdef", Map.of("maxLength", 5)));
	}

	@Test
	public void testValidateMaxLengthPasses() {
		_definition.validate("field", "abc", Map.of("maxLength", 5));
	}

	@Test
	public void testValidateMinLengthFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate("field", "ab", Map.of("minLength", 3)));
	}

	@Test
	public void testValidateMinLengthPasses() {
		_definition.validate("field", "abc", Map.of("minLength", 3));
	}

	@Test
	public void testValidateNotStringFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate("field", 123, Collections.emptyMap()));
	}

	@Test
	public void testValidatePatternFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", "ABC", Map.of("pattern", "^[a-z]+$")));
	}

	@Test
	public void testValidatePatternPasses() {
		_definition.validate("field", "abc", Map.of("pattern", "^[a-z]+$"));
	}

	@Test
	public void testValidateRequiredWithValue() {
		_definition.validate(
			"field", "hello", Map.of("required", Boolean.TRUE));
	}

	@Test
	public void testValidateString() {
		_definition.validate("field", "hello", Collections.emptyMap());
	}

	private final StringTypeDefinition _definition = new StringTypeDefinition();

}