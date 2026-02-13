/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Akhash R
 */
class DecimalTypeDefinitionTest {

	@Test
	public void testGetType() {
		Assertions.assertEquals(AttributeType.DECIMAL, _definition.getType());
	}

	@Test
	public void testResolvePostgresType() {
		Assertions.assertEquals(
			"numeric", _definition.resolvePostgresType(null));
	}

	@Test
	public void testValidateBigDecimal() {
		_definition.validate(
			"field", new BigDecimal("123.45"), Collections.emptyMap());
	}

	@Test
	public void testValidateDouble() {
		_definition.validate("field", 3.14159, Collections.emptyMap());
	}

	@Test
	public void testValidateFloat() {
		_definition.validate("field", 3.14F, Collections.emptyMap());
	}

	@Test
	public void testValidateInteger() {
		_definition.validate("field", 42, Collections.emptyMap());
	}

	@Test
	public void testValidateInvalidString() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", "abc", Map.of("precision", 10)));
	}

	@Test
	public void testValidateLong() {
		_definition.validate("field", 123456789L, Collections.emptyMap());
	}

	@Test
	public void testValidateMaxFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate("field", 200, Map.of("max", 100)));
	}

	@Test
	public void testValidateMaxPasses() {
		_definition.validate("field", 50, Map.of("max", 100));
	}

	@Test
	public void testValidateMinFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate("field", 5, Map.of("min", 10)));
	}

	@Test
	public void testValidateMinPasses() {
		_definition.validate("field", 50, Map.of("min", 10));
	}

	@Test
	public void testValidateNullValueNoConfigThrowsNpe() {
		Assertions.assertThrows(
			NullPointerException.class,
			() -> _definition.validate("field", null, Collections.emptyMap()));
	}

	@Test
	public void testValidateNullValueNullConfigThrowsNpe() {
		Assertions.assertThrows(
			NullPointerException.class,
			() -> _definition.validate("field", null, null));
	}

	@Test
	public void testValidateNumericString() {
		_definition.validate("field", "123.45", Collections.emptyMap());
	}

	@Test
	public void testValidatePrecisionFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", new BigDecimal("12345.67"), Map.of("precision", 3)));
	}

	@Test
	public void testValidatePrecisionPasses() {
		_definition.validate(
			"field", new BigDecimal("123.45"), Map.of("precision", 5));
	}

	@Test
	public void testValidateRequiredNullValue() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", null, Map.of("required", Boolean.TRUE)));
	}

	@Test
	public void testValidateScaleFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", new BigDecimal("123.456"), Map.of("scale", 2)));
	}

	@Test
	public void testValidateScalePasses() {
		_definition.validate(
			"field", new BigDecimal("123.45"), Map.of("scale", 2));
	}

	private final DecimalTypeDefinition _definition =
		new DecimalTypeDefinition();

}