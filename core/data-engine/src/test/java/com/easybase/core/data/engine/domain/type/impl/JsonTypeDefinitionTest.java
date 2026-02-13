/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Akhash R
 */
class JsonTypeDefinitionTest {

	@Test
	public void testGetType() {
		Assertions.assertEquals(AttributeType.JSON, _definition.getType());
	}

	@Test
	public void testResolvePostgresType() {
		Assertions.assertEquals("jsonb", _definition.resolvePostgresType(null));
	}

	@Test
	public void testValidateEmptyList() {
		_definition.validate(
			"field", Collections.emptyList(), Collections.emptyMap());
	}

	@Test
	public void testValidateEmptyMap() {
		_definition.validate(
			"field", Collections.emptyMap(), Collections.emptyMap());
	}

	@Test
	public void testValidateInvalidJsonString() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", "not json {", Collections.emptyMap()));
	}

	@Test
	public void testValidateJsonArrayString() {
		_definition.validate("field", "[1, 2, 3]", Collections.emptyMap());
	}

	@Test
	public void testValidateJsonObjectString() {
		_definition.validate(
			"field", "{\"key\":\"value\"}", Collections.emptyMap());
	}

	@Test
	public void testValidateList() {
		_definition.validate("field", List.of(1, 2, 3), Collections.emptyMap());
	}

	@Test
	public void testValidateMap() {
		_definition.validate(
			"field", Map.of("key", "value"), Collections.emptyMap());
	}

	@Test
	public void testValidateRequiredWithValue() {
		_definition.validate(
			"field", Map.of("key", "value"), Map.of("required", Boolean.TRUE));
	}

	private final JsonTypeDefinition _definition = new JsonTypeDefinition(
		new ObjectMapper());

}