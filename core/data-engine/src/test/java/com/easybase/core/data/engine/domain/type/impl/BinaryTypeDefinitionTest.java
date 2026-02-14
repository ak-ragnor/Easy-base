/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Akhash R
 */
class BinaryTypeDefinitionTest {

	@Test
	public void testGetType() {
		Assertions.assertEquals(AttributeType.BINARY, _definition.getType());
	}

	@Test
	public void testResolvePostgresType() {
		Assertions.assertEquals("bytea", _definition.resolvePostgresType(null));
	}

	@Test
	public void testValidateByteArray() {
		_definition.validate(
			"field", new byte[] {1, 2, 3}, Collections.emptyMap());
	}

	@Test
	public void testValidateEmptyBase64String() {
		_definition.validate("field", "", Collections.emptyMap());
	}

	@Test
	public void testValidateInvalidBase64String() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", "not-valid-base64!!!", Collections.emptyMap()));
	}

	@Test
	public void testValidateValidBase64String() {
		_definition.validate("field", "SGVsbG8=", Collections.emptyMap());
	}

	private final BinaryTypeDefinition _definition = new BinaryTypeDefinition();

}