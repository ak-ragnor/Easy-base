/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.user.controller;

import com.easybase.api.user.dto.UserDto;
import com.easybase.api.user.dto.mapper.UserMapper;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.service.UserService;
import com.easybase.infrastructure.api.dto.response.ApiResponse;

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

/**
 * @author Akhash R
 */
@CrossOrigin(origins = "*")
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

	@PostMapping
	public ResponseEntity<ApiResponse<UserDto>> createUser(
		@RequestBody @Valid UserDto request) {

		User user = _userService.createUser(
			request.getEmail(), request.getFirstName(), request.getLastName(),
			request.getDisplayName(), _serviceContext.tenantId());

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

		return ResponseEntity.ok(
			ApiResponse.success(
				_userMapper.toDto(
					_userService.getUser(email, _serviceContext.tenantId()))));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserDto>>> getUsers() {
		List<User> users = _userService.getUsers(_serviceContext.tenantId());

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

	private final ServiceContext _serviceContext;
	private final UserMapper _userMapper;
	private final UserService _userService;

}