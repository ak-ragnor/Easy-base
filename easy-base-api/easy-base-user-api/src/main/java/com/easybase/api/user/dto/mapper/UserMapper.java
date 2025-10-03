/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.user.dto.mapper;

import com.easybase.api.user.dto.UserDto;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.user.entity.User;
import com.easybase.infrastructure.api.dto.mapper.BaseMapper;

import java.time.ZoneId;

import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class UserMapper implements BaseMapper<User, UserDto> {

	@Override
	public UserDto toDto(User user) {
		if (user == null) {
			return null;
		}

		return new UserDto() {
			{
				setId(user.getId());
				setEmail(user.getEmail());
				setFirstName(user.getFirstName());
				setLastName(user.getLastName());
				setDisplayName(user.getDisplayName());

				Tenant tenant = user.getTenant();
				UUID tenantId = null;

				if (tenant != null) {
					tenantId = tenant.getId();
				}

				setTenantId(tenantId);
				setCreatedAt(
					user.getCreatedAt(
					).atZone(
						ZoneId.systemDefault()
					).toLocalDateTime());
				setUpdatedAt(
					user.getUpdatedAt(
					).atZone(
						ZoneId.systemDefault()
					).toLocalDateTime());
			}
		};
	}

	@Override
	public User toEntity(UserDto userDto) {
		if (userDto == null) {
			return null;
		}

		return new User() {
			{
				setEmail(userDto.getEmail());
				setFirstName(userDto.getFirstName());
				setLastName(userDto.getLastName());
				setDisplayName(userDto.getDisplayName());
			}
		};
	}

}