/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto.mapper;

import com.easybase.api.auth.dto.PermissionDto;
import com.easybase.api.auth.dto.RolePermissionDto;
import com.easybase.core.auth.entity.RolePermission;
import com.easybase.core.auth.helper.PermissionHelper;
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

		List<String> actionKeys =
			_permissionHelper.convertBitValuesToActionKeys(
				entity.getResourceType(), entity.getPermissionsMask());

		PermissionDto permissionDto = new PermissionDto();

		permissionDto.setResourceType(entity.getResourceType());
		permissionDto.setActions(actionKeys);

		RolePermissionDto dto = new RolePermissionDto();

		dto.setRoleId(entity.getRoleId());

		List<PermissionDto> permissions = new ArrayList<>();

		permissions.add(permissionDto);

		dto.setPermissions(permissions);

		return dto;
	}

	@Override
	public RolePermission toEntity(RolePermissionDto dto) {
		if (dto == null) {
			return null;
		}

		List<PermissionDto> permissions = dto.getPermissions();

		if ((permissions == null) || permissions.isEmpty()) {
			return null;
		}

		PermissionDto firstPermission = permissions.get(0);

		return new RolePermission(
			dto.getRoleId(), firstPermission.getResourceType());
	}

	private final PermissionHelper _permissionHelper;

}