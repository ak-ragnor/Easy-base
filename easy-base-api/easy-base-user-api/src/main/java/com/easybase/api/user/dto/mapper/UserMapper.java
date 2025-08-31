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

package com.easybase.api.user.dto.mapper;

import com.easybase.api.user.dto.UserDto;
import com.easybase.common.api.dto.mapper.BaseMapper;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.user.entity.User;

import java.util.UUID;

import org.springframework.stereotype.Component;

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
				setCreatedAt(user.getCreatedAt());
				setUpdatedAt(user.getUpdatedAt());
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