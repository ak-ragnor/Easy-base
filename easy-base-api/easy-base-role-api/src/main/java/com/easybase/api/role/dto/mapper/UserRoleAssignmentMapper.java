/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.role.dto.mapper;

import com.easybase.api.role.dto.UserRoleAssignmentDto;
import com.easybase.core.role.entity.UserRole;
import com.easybase.infrastructure.api.dto.mapper.BaseMapper;

import org.springframework.stereotype.Component;

/**
 * Mapper for UserRole entity and UserRoleAssignmentDto.
 *
 * @author Akhash R
 */
@Component
public class UserRoleAssignmentMapper
	implements BaseMapper<UserRole, UserRoleAssignmentDto> {

	@Override
	public UserRoleAssignmentDto toDto(UserRole entity) {
		if (entity == null) {
			return null;
		}

		UserRoleAssignmentDto dto = new UserRoleAssignmentDto();

		dto.setUserId(entity.getUserId());
		dto.setRoleId(entity.getRoleId());
		dto.setTenantId(entity.getTenantId());
		dto.setActive(entity.isActive());
		dto.setAssignedAt(entity.getCreatedAt());
		dto.setAssignedBy(entity.getCreatedBy());
		dto.setExpiresAt(entity.getExpiresAt());

		return dto;
	}

	@Override
	public UserRole toEntity(UserRoleAssignmentDto dto) {
		if (dto == null) {
			return null;
		}

		UserRole entity = new UserRole();

		entity.setUserId(dto.getUserId());
		entity.setRoleId(dto.getRoleId());
		entity.setTenantId(dto.getTenantId());
		entity.setExpiresAt(dto.getExpiresAt());

		return entity;
	}

}