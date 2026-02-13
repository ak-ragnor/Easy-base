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
class BooleanTypeDefinitionTest {

	@Test
	public void testGetType() {
		Assertions.assertEquals(AttributeType.BOOLEAN, _definition.getType());
	}

	@Test
	public void testResolvePostgresType() {
		Assertions.assertEquals(
			"boolean", _definition.resolvePostgresType(null));
	}

	@Test
	public void testValidateBooleanFalse() {
		_definition.validate("field", Boolean.FALSE, Collections.emptyMap());
	}

	@Test
	public void testValidateBooleanTrue() {
		_definition.validate("field", Boolean.TRUE, Collections.emptyMap());
	}

	@Test
	public void testValidateInvalidStringMaybe() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", "maybe", Collections.emptyMap()));
	}

	@Test
	public void testValidateInvalidStringOne() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate("field", "1", Collections.emptyMap()));
	}

	@Test
	public void testValidateInvalidStringYes() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate("field", "yes", Collections.emptyMap()));
	}

	@Test
	public void testValidateRequiredWithValue() {
		_definition.validate("field", true, Map.of("required", Boolean.TRUE));
	}

	@Test
	public void testValidateStringFalse() {
		_definition.validate("field", "false", Collections.emptyMap());
	}

	@Test
	public void testValidateStringFalseUpperCase() {
		_definition.validate("field", "FALSE", Collections.emptyMap());
	}

	@Test
	public void testValidateStringTrue() {
		_definition.validate("field", "true", Collections.emptyMap());
	}

	@Test
	public void testValidateStringTrueMixedCase() {
		_definition.validate("field", "True", Collections.emptyMap());
	}

	private final BooleanTypeDefinition _definition =
		new BooleanTypeDefinition();

}