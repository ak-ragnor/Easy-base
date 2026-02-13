/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Akhash R
 */
class TemporalTypeDefinitionTest {

	@Test
	void testGetType() {
		Assertions.assertEquals(AttributeType.TEMPORAL, _definition.getType());
	}

	@Test
	void testResolvePostgresTypeNoConfig() {
		Assertions.assertEquals(
			"timestamp",
			_definition.resolvePostgresType(Collections.emptyMap()));
	}

	@Test
	void testResolvePostgresTypeNullConfig() {
		Assertions.assertEquals(
			"timestamp", _definition.resolvePostgresType(null));
	}

	@Test
	void testResolvePostgresTypeDate() {
		Assertions.assertEquals(
			"date", _definition.resolvePostgresType(Map.of("format", "DATE")));
	}

	@Test
	void testResolvePostgresTypeTime() {
		Assertions.assertEquals(
			"time", _definition.resolvePostgresType(Map.of("format", "TIME")));
	}

	@Test
	void testResolvePostgresTypeDefaultFormat() {
		Assertions.assertEquals(
			"timestamp",
			_definition.resolvePostgresType(Map.of("format", "DATETIME")));
	}

	@Test
	void testValidateLocalDateTime() {
		_definition.validate(
			"field", LocalDateTime.now(), Collections.emptyMap());
	}

	@Test
	void testValidateLocalDate() {
		_definition.validate("field", LocalDate.now(), Collections.emptyMap());
	}

	@Test
	void testValidateLocalTime() {
		_definition.validate("field", LocalTime.now(), Collections.emptyMap());
	}

	@Test
	void testValidateDateTimeString() {
		_definition.validate(
			"field", "2024-01-15T10:30:00", Collections.emptyMap());
	}

	@Test
	void testValidateDateString() {
		_definition.validate("field", "2024-01-15", Collections.emptyMap());
	}

	@Test
	void testValidateTimeString() {
		_definition.validate("field", "10:30:00", Collections.emptyMap());
	}

	@Test
	void testValidateInvalidString() {
		Assertions.assertThrows(
			ValidationException.class,
			() -> _definition.validate(
				"field", "not-a-date", Collections.emptyMap()));
	}

	@Test
	void testValidateRequiredWithValue() {
		_definition.validate(
			"field", "2024-01-15T10:30:00", Map.of("required", Boolean.TRUE));
	}

	private final TemporalTypeDefinition _definition =
		new TemporalTypeDefinition();

}