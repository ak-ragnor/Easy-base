/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type;

import com.easybase.core.data.engine.domain.enums.AttributeType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
@Slf4j
public class AttributeTypeDefinitionRegistry {

	public AttributeTypeDefinitionRegistry(
		List<AttributeTypeDefinition> descriptors) {

		_descriptorMap = new EnumMap<>(AttributeType.class);

		for (AttributeTypeDefinition descriptor : descriptors) {
			AttributeType type = descriptor.getType();

			if (_descriptorMap.containsKey(type)) {
				log.warn("Duplicate descriptor for type {}, overwriting", type);
			}

			_descriptorMap.put(type, descriptor);
		}

		log.info(
			"Registered {} attribute type descriptors", _descriptorMap.size());
	}

	public AttributeTypeDefinition getDescriptor(AttributeType type) {
		AttributeTypeDefinition descriptor = _descriptorMap.get(type);

		if (descriptor == null) {
			throw new IllegalStateException(
				"No descriptor registered for type: " + type);
		}

		return descriptor;
	}

	private final Map<AttributeType, AttributeTypeDefinition> _descriptorMap;

}