/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto.mapper;

import com.easybase.api.auth.dto.RolePermissionDto;
import com.easybase.core.auth.entity.ResourceAction;
import com.easybase.core.auth.entity.RolePermission;
import com.easybase.core.auth.service.ResourceActionLocalService;
import com.easybase.infrastructure.api.dto.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * Mapper for RolePermission entity and RolePermissionDto.
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class RolePermissionMapper
	implements BaseMapper<RolePermission, RolePermissionDto> {

	@Override
	public RolePermissionDto toDto(RolePermission entity) {
		if (entity == null) {
			return null;
		}

		RolePermissionDto dto = new RolePermissionDto();

		dto.setRoleId(entity.getRoleId());
		dto.setResourceType(entity.getResourceType());

		List<ResourceAction> actions =
			_resourceActionLocalService.getActiveResourceActions(
				entity.getResourceType());

		List<String> grantedActions = new ArrayList<>();

		for (ResourceAction action : actions) {
			if (entity.hasPermission(action.getBitValue())) {
				grantedActions.add(action.getActionKey());
			}
		}

		dto.setGrantedActions(grantedActions);

		return dto;
	}

	@Override
	public RolePermission toEntity(RolePermissionDto dto) {
		if (dto == null) {
			return null;
		}

		RolePermission entity = new RolePermission(
			dto.getRoleId(), dto.getResourceType());

		return entity;
	}

	private final ResourceActionLocalService _resourceActionLocalService;

}