/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.data.engine.dto.mapper;

import com.easybase.api.data.engine.dto.AttributeDto;
import com.easybase.api.data.engine.dto.CollectionDto;
import com.easybase.core.data.engine.entity.Attribute;
import com.easybase.core.data.engine.entity.Collection;
import com.easybase.infrastructure.api.dto.mapper.BaseMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
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

				Instant createdAtInstant = collection.getCreatedAt();
				Instant updatedAtInstant = collection.getUpdatedAt();

				setCreatedAt(
					LocalDateTime.ofInstant(
						createdAtInstant, ZoneId.systemDefault()));
				setUpdatedAt(
					LocalDateTime.ofInstant(
						updatedAtInstant, ZoneId.systemDefault()));

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