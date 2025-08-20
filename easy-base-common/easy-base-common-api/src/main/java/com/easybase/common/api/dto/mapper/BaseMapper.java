package com.easybase.common.api.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface BaseMapper<E, D> {

	D toDto(E entity);

	E toEntity(D dto);

	default List<D> toDto(List<E> entities) {
		return entities == null ? List.of()
				: entities.stream().map(this::toDto)
						.collect(Collectors.toList());
	}

	default List<E> toEntity(List<D> dtos) {
		return dtos == null ? List.of()
				: dtos.stream().map(this::toEntity)
						.collect(Collectors.toList());
	}
}
