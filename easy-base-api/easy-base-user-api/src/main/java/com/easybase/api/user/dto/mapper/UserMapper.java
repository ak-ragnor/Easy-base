package com.easybase.api.user.dto.mapper;

import org.springframework.stereotype.Component;

import com.easybase.api.user.dto.UserDto;
import com.easybase.common.api.dto.mapper.BaseMapper;
import com.easybase.core.user.entity.User;

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
				setTenantId(user.getTenant() != null ? user.getTenant().getId()
						: null);
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
