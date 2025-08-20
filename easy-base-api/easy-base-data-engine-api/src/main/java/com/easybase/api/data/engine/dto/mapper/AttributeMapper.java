package com.easybase.api.data.engine.dto.mapper;

import org.springframework.stereotype.Component;

import com.easybase.api.data.engine.dto.AttributeDto;
import com.easybase.common.api.dto.mapper.BaseMapper;
import com.easybase.core.data.engine.entity.Attribute;

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
				setIndexed(Boolean.TRUE.equals(attribute.getIsIndexed()));
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
		attribute.setIsIndexed(attributeDto.isIndexed());

		return attribute;
	}
}
