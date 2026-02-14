/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Akhash R
 */
class UuidTypeDefinitionTest {

	@Test
	public void testGetType() {
		Assertions.assertEquals(AttributeType.UUID, _definition.getType());
	}

	@Test
	public void testResolvePostgresType() {
		Assertions.assertEquals("uuid", _definition.resolvePostgresType(null));
	}

	@Test
	public void testValidateInvalidUuidString() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", "not-a-uuid", Collections.emptyMap()));
	}

	@Test
	public void testValidateRequiredWithValue() {
		_definition.validate(
			"field", UUID.randomUUID(), Map.of("required", Boolean.TRUE));
	}

	@Test
	public void testValidateUuidInstance() {
		_definition.validate(
			"field", UUID.randomUUID(), Collections.emptyMap());
	}

	@Test
	public void testValidateValidUuidString() {
		_definition.validate(
			"field", "550e8400-e29b-41d4-a716-446655440000",
			Collections.emptyMap());
	}

	private final UuidTypeDefinition _definition = new UuidTypeDefinition();

}