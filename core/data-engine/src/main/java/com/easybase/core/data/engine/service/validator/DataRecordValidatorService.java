/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.validator;

import com.easybase.core.data.engine.domain.entity.Attribute;
import com.easybase.core.data.engine.domain.entity.Collection;
import com.easybase.core.data.engine.domain.type.AttributeTypeDefinition;
import com.easybase.core.data.engine.domain.type.AttributeTypeDefinitionRegistry;
import com.easybase.core.data.engine.service.CollectionLocalService;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class DataRecordValidatorService {

	public void validate(
		UUID tenantId, String collectionName, Map<String, Object> data) {

		Collection collection = _collectionLocalService.fetchCollection(
			tenantId, collectionName);

		if (collection == null) {
			log.warn("Collection not found for validation: {}", collectionName);

			return;
		}

		if (collection.getAttributes() == null) {
			return;
		}

		for (Attribute attribute : collection.getAttributes()) {
			_validateAttribute(attribute, data);
		}
	}

	private void _validateAttribute(
		Attribute attribute, Map<String, Object> data) {

		String fieldName = attribute.getName();

		Object value = data.get(fieldName);

		if (value == null) {
			return;
		}

		Map<String, Object> config = attribute.getConfig();

		if (config == null) {
			config = Collections.emptyMap();
		}

		AttributeTypeDefinition attributeTypeDefinition =
			_descriptorRegistry.getDescriptor(attribute.getDataType());

		attributeTypeDefinition.validate(fieldName, value, config);
	}

	private final CollectionLocalService _collectionLocalService;
	private final AttributeTypeDefinitionRegistry _descriptorRegistry;

}