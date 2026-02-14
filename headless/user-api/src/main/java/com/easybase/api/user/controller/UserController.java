/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.user.controller;

import com.easybase.api.user.dto.UserDto;
import com.easybase.api.user.dto.mapper.UserMapper;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.core.user.domain.entity.User;
import com.easybase.core.user.service.UserService;
import com.easybase.infrastructure.api.dto.response.ApiResponse;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user management operations.
 *
 * @author Akhash R
 */
@CrossOrigin(origins = "*")
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

	/**
	 * Delete a user.
	 *
	 * @param userId the user ID
	 */
	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable UUID userId) {
		log.debug("Deleting user: {}", userId);

		_userService.deleteUser(userId);
	}

	/**
	 * Get a user by email address.
	 *
	 * @param email the user's email address
	 * @return the user
	 */
	@GetMapping("/by-email")
	public ApiResponse<UserDto> getUserByEmail(@RequestParam String email) {
		log.debug("Fetching user by email: {}", email);

		return ApiResponse.success(
			_userMapper.toDto(
				_userService.getUser(email, _serviceContext.tenantId())));
	}

	/**
	 * Get a user by ID.
	 *
	 * @param userId the user ID
	 * @return the user
	 */
	@GetMapping("/{userId}")
	public ApiResponse<UserDto> getUserById(@PathVariable UUID userId) {
		log.debug("Fetching user: {}", userId);

		return ApiResponse.success(
			_userMapper.toDto(_userService.getUser(userId)));
	}

	/**
	 * Get all users for the current tenant.
	 *
	 * @return list of users
	 */
	@GetMapping
	public ApiResponse<List<UserDto>> getUsers() {
		log.debug(
			"Fetching all users for tenant: {}", _serviceContext.tenantId());

		return ApiResponse.success(
			_userMapper.toDto(
				_userService.getUsers(_serviceContext.tenantId())));
	}

	/**
	 * Create a new user.
	 *
	 * @param userDto the user data
	 * @return the created user
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<UserDto> postUser(@RequestBody @Valid UserDto userDto) {
		log.debug("Creating user with email: {}", userDto.getEmail());

		User user = _userService.createUser(
			userDto.getEmail(), userDto.getFirstName(), userDto.getLastName(),
			userDto.getDisplayName(), _serviceContext.tenantId());

		return ApiResponse.success(_userMapper.toDto(user));
	}

	/**
	 * Update an existing user.
	 *
	 * @param userId the user ID
	 * @param userDto the updated user data
	 * @return the updated user
	 */
	@PutMapping("/{userId}")
	public ApiResponse<UserDto> putUser(
		@PathVariable UUID userId, @RequestBody @Valid UserDto userDto) {

		log.debug("Updating user: {}", userId);

		User user = _userService.updateUser(
			userId, userDto.getFirstName(), userDto.getLastName(),
			userDto.getDisplayName());

		return ApiResponse.success(_userMapper.toDto(user));
	}

	private final ServiceContext _serviceContext;
	private final UserMapper _userMapper;
	private final UserService _userService;

}