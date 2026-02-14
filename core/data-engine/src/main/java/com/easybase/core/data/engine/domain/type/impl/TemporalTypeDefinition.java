/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type.impl;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.domain.enums.AttributeType;
import com.easybase.core.data.engine.domain.type.AttributeTypeDefinition;
import com.easybase.core.data.engine.domain.validation.RequiredValidator;
import com.easybase.core.data.engine.domain.validation.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
@Slf4j
public class TemporalTypeDefinition implements AttributeTypeDefinition {

	@Override
	public AttributeType getType() {
		return AttributeType.TEMPORAL;
	}

	@Override
	public String resolvePostgresType(Map<String, Object> config) {
		if (config == null) {
			return "timestamp";
		}

		Object format = config.get("format");

		if (format == null) {
			return "timestamp";
		}

		String formatStr = format.toString();

		formatStr = formatStr.toUpperCase();

		switch (formatStr) {
			case "DATE":
				return "date";
			case "TIME":
				return "time";
			default:
				return "timestamp";
		}
	}

	@Override
	public void validate(
		String fieldName, Object value, Map<String, Object> config) {

		_validateType(fieldName, value);

		for (Validator validator : _validators) {
			validator.validate(fieldName, value, config);
		}
	}

	private void _validateType(String fieldName, Object value) {
		if (value == null) {
			return;
		}

		if (value instanceof LocalDate || value instanceof LocalDateTime ||
			value instanceof LocalTime) {

			return;
		}

		String str = value.toString();

		try {
			LocalDateTime.parse(str);

			return;
		}
		catch (Exception exception) {
			log.debug("Failed to parse '{}' as LocalDateTime", str);
		}

		try {
			LocalDate.parse(str);

			return;
		}
		catch (Exception exception) {
			log.debug("Failed to parse '{}' as LocalDate", str);
		}

		try {
			LocalTime.parse(str);

			return;
		}
		catch (Exception exception) {
			log.debug("Failed to parse '{}' as LocalTime", str);
		}

		throw new ValidationException(
			fieldName, str, "expected a valid temporal value (ISO-8601)");
	}

	private final List<Validator> _validators = List.of(
		new RequiredValidator());

}