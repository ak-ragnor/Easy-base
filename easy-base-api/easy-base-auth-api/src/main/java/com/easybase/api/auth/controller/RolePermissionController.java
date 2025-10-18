/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.controller;

import com.easybase.api.auth.dto.PermissionDto;
import com.easybase.api.auth.dto.RolePermissionDto;
import com.easybase.api.auth.dto.mapper.RolePermissionMapper;
import com.easybase.core.auth.entity.RolePermission;
import com.easybase.core.auth.helper.PermissionHelper;
import com.easybase.core.auth.service.RolePermissionService;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for role permission management operations.
 *
 * @author Akhash R
 */
@RequestMapping("/roles/{roleId}/permissions")
@RequiredArgsConstructor
@RestController
public class RolePermissionController {

	/**
	 * Check if a role has specific permissions for a resource type.
	 *
	 * @param roleId the role ID from path
	 * @param permissionDto the permission check DTO
	 * @return the check result with hasPermission populated
	 */
	@PostMapping("/check")
	public PermissionDto checkPermissions(
		@PathVariable("roleId") UUID roleId,
		@RequestBody @Valid PermissionDto permissionDto) {

		boolean hasPermission = _rolePermissionService.checkPermissions(
			roleId, permissionDto.getResourceType(),
			permissionDto.getActions());

		permissionDto.setHasPermission(hasPermission);

		return permissionDto;
	}

	/**
	 * Delete all permissions for a role.
	 *
	 * @param roleId the role ID
	 */
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllPermissionsForRole(@PathVariable("roleId") UUID roleId) {
		_rolePermissionService.deleteAllPermissionsForRole(roleId);
	}

	/**
	 * Delete permissions for a role and specific resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 */
	@DeleteMapping("/{resourceType}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePermissionsForResourceType(
		@PathVariable("roleId") UUID roleId, @PathVariable("resourceType") String resourceType) {

		_rolePermissionService.deletePermissionsForRoleAndResource(
			roleId, resourceType);
	}

	/**
	 * Get all permissions for a role across all resource types.
	 *
	 * @param roleId the role ID
	 * @return list of role permissions
	 */
	@GetMapping
	public RolePermissionDto getAllPermissions(@PathVariable("roleId") UUID roleId) {
		List<RolePermission> permissions =
			_rolePermissionService.getPermissionsForRole(roleId);

		if ((permissions == null) || permissions.isEmpty()) {
			RolePermissionDto emptyDto = new RolePermissionDto();

			emptyDto.setRoleId(roleId);
			emptyDto.setPermissions(List.of());

			return emptyDto;
		}

		List<PermissionDto> permissionDtos = new ArrayList<>();

		for (RolePermission permission : permissions) {
			List<String> actionKeys =
				_permissionHelper.convertBitValuesToActionKeys(
					permission.getResourceType(),
					permission.getPermissionsMask());

			PermissionDto permissionDto = new PermissionDto();

			permissionDto.setResourceType(permission.getResourceType());
			permissionDto.setActions(actionKeys);

			permissionDtos.add(permissionDto);
		}

		RolePermissionDto dto = new RolePermissionDto();

		dto.setRoleId(roleId);
		dto.setPermissions(permissionDtos);

		return dto;
	}

	/**
	 * Get permissions for a role and specific resource type.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @return the role permission
	 */
	@GetMapping("/{resourceType}")
	public ResponseEntity<RolePermissionDto> getPermissionsForResourceType(
		@PathVariable("roleId") UUID roleId, @PathVariable("resourceType") String resourceType) {

		RolePermission permission =
			_rolePermissionService.getPermissionsForRoleAndResource(
				roleId, resourceType);

		if (permission == null) {
			ResponseEntity.HeadersBuilder<?> responseBuilder =
				ResponseEntity.notFound();

			return responseBuilder.build();
		}

		return ResponseEntity.ok(_rolePermissionMapper.toDto(permission));
	}

	/**
	 * Grant (add) specific permissions to a role for one or more resource types.
	 * Adds to existing permissions without removing any.
	 *
	 * @param roleId the role ID
	 * @param dto the role permission DTO
	 * @return the updated permissions
	 */
	@PostMapping("/grant")
	public RolePermissionDto grantPermissions(
		@PathVariable("roleId") UUID roleId, @RequestBody @Valid RolePermissionDto dto) {

		if (!roleId.equals(dto.getRoleId())) {
			throw new IllegalArgumentException(
				"Role ID in path does not match role ID in request body");
		}

		for (PermissionDto permission : dto.getPermissions()) {
			_rolePermissionService.grantPermissionsByActionKeys(
				roleId, permission.getResourceType(), permission.getActions());
		}

		return getAllPermissions(roleId);
	}

	/**
	 * Revoke (remove) specific permissions from a role for one or more resource types.
	 * Removes specified permissions while keeping others.
	 *
	 * @param roleId the role ID
	 * @param dto the role permission DTO
	 * @return the updated permissions
	 */
	@PostMapping("/revoke")
	public RolePermissionDto revokePermissions(
		@PathVariable("roleId") UUID roleId, @RequestBody @Valid RolePermissionDto dto) {

		if (!roleId.equals(dto.getRoleId())) {
			throw new IllegalArgumentException(
				"Role ID in path does not match role ID in request body");
		}

		for (PermissionDto permission : dto.getPermissions()) {
			_rolePermissionService.revokePermissionsByActionKeys(
				roleId, permission.getResourceType(), permission.getActions());
		}

		return getAllPermissions(roleId);
	}

	/**
	 * Set (replace) all permissions for a role and specific resource type.
	 * Replaces all existing permissions for that resource type with the new set.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param permissionDto the permission DTO
	 * @return the updated permission for that resource type
	 */
	@PutMapping("/{resourceType}")
	public RolePermissionDto setPermissions(
		@PathVariable("roleId") UUID roleId, @PathVariable("resourceType") String resourceType,
		@RequestBody @Valid PermissionDto permissionDto) {

		if (!resourceType.equals(permissionDto.getResourceType())) {
			throw new IllegalArgumentException(
				"Resource type in path does not match resource type in request body");
		}

		RolePermission rolePermission =
			_rolePermissionService.setPermissionsByActionKeys(
				roleId, resourceType, permissionDto.getActions());

		return _rolePermissionMapper.toDto(rolePermission);
	}

	private final PermissionHelper _permissionHelper;
	private final RolePermissionMapper _rolePermissionMapper;
	private final RolePermissionService _rolePermissionService;

}