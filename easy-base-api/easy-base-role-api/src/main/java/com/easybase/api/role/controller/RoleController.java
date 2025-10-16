/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.role.controller;

import com.easybase.api.role.dto.RoleDto;
import com.easybase.api.role.dto.UserRoleAssignmentDto;
import com.easybase.api.role.dto.mapper.RoleMapper;
import com.easybase.api.role.dto.mapper.UserRoleAssignmentMapper;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.core.role.entity.Role;
import com.easybase.core.role.entity.UserRole;
import com.easybase.core.role.service.RoleService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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
 * REST controller for role management operations.
 *
 * @author Akhash R
 */
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@RestController
public class RoleController {

	/**
	 * Delete a role.
	 *
	 * @param roleId the role ID
	 */
	@DeleteMapping("/{roleId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRole(@PathVariable UUID roleId) {
		_roleService.deleteRole(roleId);
	}

	/**
	 * Unassign a role from a user.
	 *
	 * @param roleId the role ID
	 * @param userId the user ID
	 */
	@DeleteMapping("/{roleId}/users/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUserRoleAssignment(
		@PathVariable UUID roleId, @PathVariable UUID userId) {

		_roleService.revokeRoleFromUser(userId, roleId);
	}

	/**
	 * Get a specific role by ID.
	 *
	 * @param roleId the role ID
	 * @return the role
	 */
	@GetMapping("/{roleId}")
	public RoleDto getRoleById(@PathVariable UUID roleId) {
		Role role = _roleService.getRoleById(roleId);

		return _roleMapper.toDto(role);
	}

	/**
	 * Get all available roles
	 *
	 * @return list of roles
	 */
	@GetMapping
	public List<RoleDto> getRoles() {

		List<Role> roles = _roleService.getAvailableRoles(_serviceContext.tenantId());

		return _roleMapper.toDto(roles);
	}

	/**
	 * Get all role assignments for a specific user.
	 *
	 * @param userId the user ID
	 * @return list of user role assignments
	 */
	@GetMapping("/users/{userId}")
	public List<UserRoleAssignmentDto> getUserRoleAssignments(
		@PathVariable UUID userId) {

		List<UserRole> userRoles = _roleService.getUserRoles(userId);

		return _userRoleAssignmentMapper.toDto(userRoles);
	}

	/**
	 * Create a new role.
	 *
	 * @param dto the role DTO
	 * @return the created role
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RoleDto postRole(@RequestBody @Valid RoleDto dto) {
		Role role = _roleService.createRole(
			dto.getName(), dto.getDescription(), _serviceContext.tenantId(),
			false);

		return _roleMapper.toDto(role);
	}

	/**
	 * Assign a role to a user.
	 *
	 * @param roleId the role ID
	 * @param dto the assignment DTO containing userId, tenantId, expiresAt
	 * @return the created assignment
	 */
	@PostMapping("/{roleId}/users")
	@ResponseStatus(HttpStatus.CREATED)
	public UserRoleAssignmentDto postUserRoleAssignment(
		@PathVariable UUID roleId,
		@RequestBody @Valid UserRoleAssignmentDto dto) {

		if (!roleId.equals(dto.getRoleId())) {
			throw new IllegalArgumentException(
				"Role ID in path does not match role ID in request body");
		}

		UserRole userRole = _roleService.assignRoleToUser(
			dto.getUserId(), roleId, _serviceContext.tenantId(), dto.getExpiresAt());

		return _userRoleAssignmentMapper.toDto(userRole);
	}

	/**
	 * Update an existing role.
	 *
	 * @param roleId the role ID
	 * @param dto the role DTO with updated fields
	 * @return the updated role
	 */
	@PutMapping("/{roleId}")
	public RoleDto putRole(
		@PathVariable UUID roleId, @RequestBody @Valid RoleDto dto) {

		Role role = _roleService.updateRole(roleId, dto.getDescription());

		return _roleMapper.toDto(role);
	}

	private final RoleMapper _roleMapper;
	private final ServiceContext _serviceContext;
	private final RoleService _roleService;
	private final UserRoleAssignmentMapper _userRoleAssignmentMapper;

}