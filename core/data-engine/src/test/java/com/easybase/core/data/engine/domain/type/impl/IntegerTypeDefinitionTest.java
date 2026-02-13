/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;

import java.math.BigInteger;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Akhash R
 */
class IntegerTypeDefinitionTest {

	@Test
	public void testGetType() {
		Assertions.assertEquals(AttributeType.INTEGER, _definition.getType());
	}

	@Test
	public void testResolvePostgresTypeEmptyConfig() {
		Assertions.assertEquals(
			"integer", _definition.resolvePostgresType(Collections.emptyMap()));
	}

	@Test
	public void testResolvePostgresTypeMaxAboveIntRange() {
		Assertions.assertEquals(
			"bigint",
			_definition.resolvePostgresType(
				Map.of("max", (long)Integer.MAX_VALUE + 1)));
	}

	@Test
	public void testResolvePostgresTypeMinBelowIntRange() {
		Assertions.assertEquals(
			"bigint",
			_definition.resolvePostgresType(
				Map.of("min", (long)Integer.MIN_VALUE - 1)));
	}

	@Test
	public void testResolvePostgresTypeNoConfig() {
		Assertions.assertEquals(
			"integer", _definition.resolvePostgresType(null));
	}

	@Test
	public void testResolvePostgresTypeWithinIntRange() {
		Assertions.assertEquals(
			"integer",
			_definition.resolvePostgresType(Map.of("min", 0, "max", 1000)));
	}

	@Test
	public void testValidateBigInteger() {
		_definition.validate(
			"field", BigInteger.valueOf(999), Collections.emptyMap());
	}

	@Test
	public void testValidateByte() {
		_definition.validate("field", (byte)5, Collections.emptyMap());
	}

	@Test
	public void testValidateInteger() {
		_definition.validate("field", 42, Collections.emptyMap());
	}

	@Test
	public void testValidateInvalidString() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate("field", "abc", Collections.emptyMap()));
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
			() -> _definition.validate("field", 3, Map.of("min", 5)));
	}

	@Test
	public void testValidateMinPasses() {
		_definition.validate("field", 10, Map.of("min", 5));
	}

	@Test
	public void testValidateNullValueThrowsNpe() {
		Assertions.assertThrows(
			NullPointerException.class,
			() -> _definition.validate("field", null, Collections.emptyMap()));
	}

	@Test
	public void testValidateNumberWithFractionFails() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate("field", 3.5, Collections.emptyMap()));
	}

	@Test
	public void testValidateNumberWithNoFraction() {
		_definition.validate("field", 10.0, Collections.emptyMap());
	}

	@Test
	public void testValidateRequiredNullValue() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", null, Map.of("required", Boolean.TRUE)));
	}

	@Test
	public void testValidateShort() {
		_definition.validate("field", (short)10, Collections.emptyMap());
	}

	@Test
	public void testValidateValidIntegerString() {
		_definition.validate("field", "12345", Collections.emptyMap());
	}

	private final IntegerTypeDefinition _definition =
		new IntegerTypeDefinition();

}