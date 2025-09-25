/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.role.controller;

import com.easybase.api.role.dto.RoleDto;
import com.easybase.api.role.dto.UserRoleAssignmentDto;
import com.easybase.core.role.entity.Role;
import com.easybase.core.role.entity.UserRole;
import com.easybase.core.role.service.RoleService;

import jakarta.validation.Valid;

import java.time.Instant;

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

	@PostMapping("/{roleId}/assign")
	@ResponseStatus(HttpStatus.CREATED)
	public UserRoleAssignmentDto assignRole(
		@PathVariable UUID roleId, @RequestParam UUID userId,
		@RequestParam(required = false) UUID tenantId,
		@RequestParam(required = false) Instant expiresAt) {

		UserRole userRole = roleService.assignRoleToUser(
			userId, roleId, tenantId, expiresAt);
		return convertToUserRoleAssignmentDto(userRole);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RoleDto createRole(@RequestBody @Valid RoleDto roleDto) {
		Role role = roleService.createRole(
			roleDto.getName(),
			roleDto.getDescription(),
			roleDto.getTenantId(),
			roleDto.isSystem());
		return convertToRoleDto(role);
	}

	@DeleteMapping("/{roleId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRole(@PathVariable UUID roleId) {
		roleService.deleteRole(roleId);
	}

	@GetMapping
	public List<RoleDto> getAvailableRoles(
		@RequestParam(required = false) UUID tenantId) {

		List<Role> roles = roleService.getAvailableRoles(tenantId);
		return roles.stream().map(this::convertToRoleDto).toList();
	}

	@GetMapping("/{roleId}")
	public RoleDto getRoleById(@PathVariable UUID roleId) {
		Role role = roleService.getRoleById(roleId);
		return convertToRoleDto(role);
	}

	@GetMapping("/user/{userId}")
	public List<UserRoleAssignmentDto> getUserRoles(@PathVariable UUID userId) {
		List<UserRole> userRoles = roleService.getUserRoles(userId);
		return userRoles.stream().map(this::convertToUserRoleAssignmentDto).toList();
	}

	@PostMapping("/{roleId}/unassign")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unassignRole(
		@PathVariable UUID roleId, @RequestParam UUID userId,
		@RequestParam(required = false) UUID tenantId) {

		roleService.revokeRoleFromUser(userId, roleId);
	}

	@PutMapping("/{roleId}")
	public RoleDto updateRole(
		@PathVariable UUID roleId, @RequestBody @Valid RoleDto roleDto) {

		Role role = roleService.updateRole(roleId, roleDto.getDescription());
		return convertToRoleDto(role);
	}

	private RoleDto convertToRoleDto(Role role) {
		RoleDto dto = new RoleDto();
		dto.setId(role.getId());
		dto.setName(role.getName());
		dto.setDescription(role.getDescription());
		dto.setTenantId(role.getTenant() != null ? role.getTenant().getId() : null);
		dto.setSystem(role.isSystem());
		dto.setActive(role.isActive());
		dto.setCreatedDate(role.getCreatedAt() != null ?
			role.getCreatedAt().toInstant(java.time.ZoneOffset.UTC) : null);
		dto.setLastModifiedDate(role.getUpdatedAt() != null ?
			role.getUpdatedAt().toInstant(java.time.ZoneOffset.UTC) : null);
		return dto;
	}

	private UserRoleAssignmentDto convertToUserRoleAssignmentDto(UserRole userRole) {
		UserRoleAssignmentDto dto = new UserRoleAssignmentDto();

		dto.setUserId(userRole.getUser().getId());
		dto.setRoleId(userRole.getRole().getId());
		dto.setTenantId(userRole.getTenant() != null ? userRole.getTenant().getId() : null);
		dto.setActive(userRole.isActive());
		dto.setAssignedAt(userRole.getAssignedAt());
		dto.setAssignedBy(userRole.getAssignedBy());
		dto.setExpiresAt(userRole.getExpiresAt());

		return dto;
	}

	private final RoleService roleService;

}