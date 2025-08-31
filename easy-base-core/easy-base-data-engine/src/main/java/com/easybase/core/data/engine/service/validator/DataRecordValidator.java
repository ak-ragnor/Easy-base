/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.core.data.engine.service.validator;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.entity.Attribute;
import com.easybase.core.data.engine.entity.Collection;
import com.easybase.core.data.engine.enums.AttributeType;
import com.easybase.core.data.engine.repository.CollectionRepository;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * Validator for record data
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataRecordValidator {

	public void validate(
		UUID tenantId, String collectionName, Map<String, Object> data) {

		Optional<Collection> optionalCollection =
			_collectionRepository.findByTenantIdAndName(
				tenantId, collectionName);

		Collection collection = optionalCollection.orElse(null);

		if (collection == null) {
			log.warn("Collection not found for validation: {}", collectionName);

			return;
		}

		if (collection.getAttributes() == null) {
			return;
		}

		for (Attribute attribute : collection.getAttributes()) {
			String fieldName = attribute.getName();

			Object value = data.get(fieldName);

			if (value != null) {
				_validateType(attribute, value);
			}
		}
	}

	private void _validateType(Attribute attribute, Object value) {
		AttributeType type = attribute.getDataType();
		String fieldName = attribute.getName();

		try {
			switch (type) {
				case TEXT:
				case VARCHAR:
					value.toString();

					break;

				case INTEGER:
					if (!(value instanceof Integer)) {
						String strValue = value.toString();

						Integer.parseInt(strValue);
					}

					break;

				case BIGINT:
					if (!(value instanceof Long)) {
						Long.parseLong(value.toString());
					}

					break;

				case DECIMAL:
				case NUMERIC:
					if (!(value instanceof BigDecimal)) {
						new BigDecimal(value.toString());
					}

					break;

				case BOOLEAN:
					if (!(value instanceof Boolean)) {
						String strValue = value.toString();

						strValue = strValue.toLowerCase();

						if (!strValue.equals("true") &&
							!strValue.equals("false")) {

							throw new ValidationException(
								fieldName, value.toString(),
								"must be a valid boolean value");
						}
					}

					break;

				case DATE:
					if (!(value instanceof LocalDate)) {
						LocalDate.parse(value.toString());
					}

					break;

				case TIMESTAMP:
				case DATETIME:
					if (!(value instanceof LocalDateTime)) {
						LocalDateTime.parse(value.toString());
					}

					break;

				case UUID:
					if (!(value instanceof UUID)) {
						UUID.fromString(value.toString());
					}

					break;

				default:

					break;
			}
		}
		catch (Exception exception) {
			log.debug(
				"Validation failed for field {} with value {}: {}", fieldName,
				value, exception.getMessage());

			throw new ValidationException(
				fieldName, value.toString(),
				String.format("expected type %s", type));
		}
	}

	private final CollectionRepository _collectionRepository;

}