package com.easybase.api.user.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easybase.api.user.dto.UserDto;
import com.easybase.api.user.dto.mapper.UserMapper;
import com.easybase.common.api.dto.response.ApiResponse;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantService;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/easy-base/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

	@PostMapping
	public ResponseEntity<ApiResponse<UserDto>> createUser(
			@Valid @RequestBody UserDto request) {
		Tenant tenant = _tenantService.getDefaultTenant();

		User user = _userService.createUser(request.getEmail(),
				request.getFirstName(), request.getLastName(),
				request.getDisplayName(), tenant.getId());

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(_userMapper.toDto(user)));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> getUser(
			@PathVariable("userId") UUID userId) {
		User user = _userService.getUser(userId);

		return ResponseEntity.ok(ApiResponse.success(_userMapper.toDto(user)));
	}

	@GetMapping("/by-email")
	public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(
			@RequestParam("email") String email) {
		Tenant tenant = _tenantService.getDefaultTenant();

		User user = _userService.getUser(email, tenant.getId());

		return ResponseEntity.ok(ApiResponse.success(_userMapper.toDto(user)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserDto>>> getUsers() {
		Tenant tenant = _tenantService.getDefaultTenant();

		List<User> users = _userService.getUsers(tenant.getId());

		List<UserDto> userDtos = users.stream().map(_userMapper::toDto)
				.toList();

		return ResponseEntity.ok(ApiResponse.success(userDtos));
	}

	@PutMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> updateUser(
			@PathVariable("userId") UUID userId,
			@Valid @RequestBody UserDto request) {

		log.debug("Updating user: {}", userId);

		User user = _userService.updateUser(userId, request.getFirstName(),
				request.getLastName(), request.getDisplayName());

		return ResponseEntity.ok(ApiResponse.success(_userMapper.toDto(user)));
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse<Void>> deleteUser(
			@PathVariable("userId") UUID userId) {
		_userService.deleteUser(userId);

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	private final UserMapper _userMapper;

	private final UserService _userService;

	private final TenantService _tenantService;
}
