package com.easybase.api.data.engine.dto.mapper;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.easybase.api.data.engine.dto.CollectionDto;
import com.easybase.common.api.dto.mapper.BaseMapper;
import com.easybase.core.data.engine.entity.Collection;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CollectionMapper implements BaseMapper<Collection, CollectionDto> {

	private final AttributeMapper _attributeMapper;

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
				setAttributes(collection.getAttributes() == null
						? Collections.emptyList()
						: collection.getAttributes().stream()
								.map(_attributeMapper::toDto)
								.collect(Collectors.toList()));
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
				setAttributes(collectionDto.getAttributes() == null
						? Collections.emptyList()
						: collectionDto.getAttributes().stream()
								.map(_attributeMapper::toEntity)
								.collect(Collectors.toList()));
			}
		};
	}

}
