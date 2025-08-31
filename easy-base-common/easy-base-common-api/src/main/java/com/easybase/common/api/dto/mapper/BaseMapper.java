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

package com.easybase.common.api.dto.mapper;

import java.util.ArrayList;
import java.util.List;

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