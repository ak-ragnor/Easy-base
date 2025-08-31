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

package com.easybase.api.data.engine.dto.mapper;

import com.easybase.api.data.engine.dto.AttributeDto;
import com.easybase.common.api.dto.mapper.BaseMapper;
import com.easybase.core.data.engine.entity.Attribute;

import org.springframework.stereotype.Component;

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