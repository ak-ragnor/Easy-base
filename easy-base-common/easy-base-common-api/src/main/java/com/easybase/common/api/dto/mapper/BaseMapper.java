/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.api.dto.mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Akhash R
 */
public interface BaseMapper<E, D> {

	public D toDto(E entity);

	public default List<D> toDto(List<E> entities) {
		if (entities == null) {
			return List.of();
		}

		List<D> dtos = new ArrayList<>(entities.size());

		for (E entity : entities) {
			dtos.add(toDto(entity));
		}

		return dtos;
	}

	public E toEntity(D dto);

	public default List<E> toEntity(List<D> dtos) {
		if (dtos == null) {
			return List.of();
		}

		List<E> entities = new ArrayList<>(dtos.size());

		for (D dto : dtos) {
			entities.add(toEntity(dto));
		}

		return entities;
	}

}