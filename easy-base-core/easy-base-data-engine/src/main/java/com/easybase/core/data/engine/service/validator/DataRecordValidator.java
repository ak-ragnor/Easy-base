package com.easybase.core.data.engine.service.validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.easybase.common.exception.ValidationException;
import com.easybase.core.data.engine.entity.Attribute;
import com.easybase.core.data.engine.entity.Collection;
import com.easybase.core.data.engine.enums.AttributeType;
import com.easybase.core.data.engine.repository.CollectionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Validator for record data
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataRecordValidator {

	public void validate(UUID tenantId, String collectionName,
			Map<String, Object> data) {
		Collection collection = _collectionRepository
				.findByTenantIdAndName(tenantId, collectionName).orElse(null);

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
				validateType(attribute, value);
			}

		}
	}

	private void validateType(Attribute attribute, Object value) {
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
						Integer.parseInt(value.toString());
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
						String strValue = value.toString().toLowerCase();

						if (!strValue.equals("true")
								&& !strValue.equals("false")) {
							throw new ValidationException(fieldName,
									value.toString(),
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
		} catch (Exception e) {
			throw new ValidationException(fieldName, value.toString(),
					String.format("expected type %s", type));
		}
	}

	private final CollectionRepository _collectionRepository;
}
