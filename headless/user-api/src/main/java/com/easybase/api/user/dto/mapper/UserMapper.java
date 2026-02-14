/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.user.dto.mapper;

import com.easybase.api.user.dto.UserDto;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.user.domain.entity.User;
import com.easybase.infrastructure.api.dto.mapper.BaseMapper;

import java.time.Instant;
import java.time.LocalDateTime;
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

				Instant createdAtInstant = user.getCreatedAt();
				Instant updatedAtInstant = user.getUpdatedAt();

				if (createdAtInstant != null) {
					setCreatedAt(
						LocalDateTime.ofInstant(
							createdAtInstant, ZoneId.systemDefault()));
				}

				if (updatedAtInstant != null) {
					setUpdatedAt(
						LocalDateTime.ofInstant(
							updatedAtInstant, ZoneId.systemDefault()));
				}
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