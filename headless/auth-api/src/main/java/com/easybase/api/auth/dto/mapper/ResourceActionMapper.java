/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto.mapper;

import com.easybase.api.auth.dto.ResourceActionDto;
import com.easybase.core.auth.domain.entity.ResourceAction;
import com.easybase.infrastructure.api.dto.mapper.BaseMapper;

import org.springframework.stereotype.Component;

/**
 * Mapper for ResourceAction entity and ResourceActionDto.
 *
 * @author Akhash R
 */
@Component
public class ResourceActionMapper
	implements BaseMapper<ResourceAction, ResourceActionDto> {

	@Override
	public ResourceActionDto toDto(ResourceAction entity) {
		if (entity == null) {
			return null;
		}

		ResourceActionDto dto = new ResourceActionDto();

		dto.setId(entity.getId());
		dto.setResourceType(entity.getResourceType());
		dto.setActionKey(entity.getActionKey());
		dto.setActionName(entity.getActionName());
		dto.setDescription(entity.getDescription());
		dto.setActive(entity.isActive());
		dto.setCreatedAt(entity.getCreatedAt());
		dto.setUpdatedAt(entity.getUpdatedAt());

		return dto;
	}

	@Override
	public ResourceAction toEntity(ResourceActionDto dto) {
		if (dto == null) {
			return null;
		}

		ResourceAction entity = new ResourceAction();

		entity.setResourceType(dto.getResourceType());
		entity.setActionKey(dto.getActionKey());
		entity.setActionName(dto.getActionName());
		entity.setDescription(dto.getDescription());
		entity.setActive(dto.isActive());

		return entity;
	}

}