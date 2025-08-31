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
import com.easybase.api.data.engine.dto.CollectionDto;
import com.easybase.common.api.dto.mapper.BaseMapper;
import com.easybase.core.data.engine.entity.Attribute;
import com.easybase.core.data.engine.entity.Collection;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollectionMapper implements BaseMapper<Collection, CollectionDto> {

	@Override
	public CollectionDto toDto(Collection collection) {
		if (collection == null) {
			return null;
		}

		return new CollectionDto() {
			{
				setId(collection.getId());
				setName(collection.getName());
				setCreatedAt(collection.getCreatedAt());
				setUpdatedAt(collection.getUpdatedAt());

				if (collection.getAttributes() == null) {
					setAttributes(Collections.emptyList());
				}
				else {
					List<Attribute> attributes = collection.getAttributes();

					Stream<Attribute> attributesStream = attributes.stream();

					setAttributes(
						attributesStream.map(
							_attributeMapper::toDto
						).toList());
				}
			}
		};
	}

	@Override
	public Collection toEntity(CollectionDto collectionDto) {
		if (collectionDto == null) {
			return null;
		}

		return new Collection() {
			{
				setName(collectionDto.getName());

				if (collectionDto.getAttributes() == null) {
					setAttributes(Collections.emptyList());
				}
				else {
					List<AttributeDto> attributeDtos =
						collectionDto.getAttributes();

					Stream<AttributeDto> attributeDtosStream =
						attributeDtos.stream();

					setAttributes(
						attributeDtosStream.map(
							_attributeMapper::toEntity
						).toList());
				}
			}
		};
	}

	private final AttributeMapper _attributeMapper;

}