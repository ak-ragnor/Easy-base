/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.data.engine.dto.mapper;

import com.easybase.api.data.engine.dto.AttributeDto;
import com.easybase.common.api.dto.mapper.BaseMapper;
import com.easybase.core.data.engine.entity.Attribute;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class AttributeMapper implements BaseMapper<Attribute, AttributeDto> {

	@Override
	public AttributeDto toDto(Attribute attribute) {
		if (attribute == null) {
			return null;
		}

		return new AttributeDto() {
			{
				setId(attribute.getId());
				setName(attribute.getName());
				setType(attribute.getDataType());
				setIndexed(Boolean.TRUE.equals(attribute.getIndexed()));
			}
		};
	}

	@Override
	public Attribute toEntity(AttributeDto attributeDto) {
		if (attributeDto == null) {
			return null;
		}

		Attribute attribute = new Attribute();

		attribute.setName(attributeDto.getName());
		attribute.setDataType(attributeDto.getType());
		attribute.setIndexed(attributeDto.isIndexed());

		return attribute;
	}

}