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

package com.easybase.api.user.controller;

import com.easybase.api.user.dto.UserDto;
import com.easybase.api.user.dto.mapper.UserMapper;
import com.easybase.common.api.dto.response.ApiResponse;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantService;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.service.UserService;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RequestMapping("/easy-base/api/users")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

	@PostMapping
	public ResponseEntity<ApiResponse<UserDto>> createUser(
		@RequestBody @Valid UserDto request) {

		Tenant tenant = _tenantService.getDefaultTenant();

		User user = _userService.createUser(
			request.getEmail(), request.getFirstName(), request.getLastName(),
			request.getDisplayName(), tenant.getId());

		ApiResponse<UserDto> response = ApiResponse.success(
			_userMapper.toDto(user));

		ResponseEntity.BodyBuilder responseEntity = ResponseEntity.status(
			HttpStatus.CREATED);

		return responseEntity.body(response);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse<Void>> deleteUser(
		@PathVariable("userId") UUID userId) {

		_userService.deleteUser(userId);

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> getUser(
		@PathVariable("userId") UUID userId) {

		return ResponseEntity.ok(
			ApiResponse.success(
				_userMapper.toDto(_userService.getUser(userId))));
	}

	@GetMapping("/by-email")
	public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(
		@RequestParam("email") String email) {

		Tenant tenant = _tenantService.getDefaultTenant();

		return ResponseEntity.ok(
			ApiResponse.success(
				_userMapper.toDto(
					_userService.getUser(email, tenant.getId()))));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserDto>>> getUsers() {
		Tenant tenant = _tenantService.getDefaultTenant();

		List<User> users = _userService.getUsers(tenant.getId());

		List<UserDto> userDtos = new ArrayList<>();

		for (User user : users) {
			userDtos.add(_userMapper.toDto(user));
		}

		ApiResponse<List<UserDto>> response = ApiResponse.success(userDtos);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> updateUser(
		@PathVariable("userId") UUID userId,
		@RequestBody @Valid UserDto request) {

		log.debug("Updating user: {}", userId);

		User user = _userService.updateUser(
			userId, request.getFirstName(), request.getLastName(),
			request.getDisplayName());

		return ResponseEntity.ok(ApiResponse.success(_userMapper.toDto(user)));
	}

	private final TenantService _tenantService;
	private final UserMapper _userMapper;
	private final UserService _userService;

}