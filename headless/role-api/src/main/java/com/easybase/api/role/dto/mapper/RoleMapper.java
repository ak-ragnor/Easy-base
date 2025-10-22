/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.role.dto.mapper;

import com.easybase.api.role.dto.RoleDto;
import com.easybase.core.role.entity.Role;
import com.easybase.infrastructure.api.dto.mapper.BaseMapper;

import org.springframework.stereotype.Component;

/**
 * Mapper for Role entity and RoleDto.
 *
 * @author Akhash R
 */
@Component
public class RoleMapper implements BaseMapper<Role, RoleDto> {

	@Override
	public RoleDto toDto(Role entity) {
		if (entity == null) {
			return null;
		}

		RoleDto dto = new RoleDto();

		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setDescription(entity.getDescription());
		dto.setTenantId(entity.getTenantId());
		dto.setSystem(entity.isSystem());
		dto.setActive(entity.isActive());
		dto.setCreatedDate(entity.getCreatedAt());
		dto.setLastModifiedDate(entity.getUpdatedAt());

		return dto;
	}

	@Override
	public Role toEntity(RoleDto dto) {
		if (dto == null) {
			return null;
		}

		Role entity = new Role();

		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setTenantId(dto.getTenantId());
		entity.setSystem(dto.isSystem());

		return entity;
	}

}