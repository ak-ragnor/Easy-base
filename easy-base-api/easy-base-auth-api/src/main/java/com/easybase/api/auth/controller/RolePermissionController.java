/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.controller;

import com.easybase.api.auth.dto.CheckPermissionRequest;
import com.easybase.api.auth.dto.CheckPermissionResponse;
import com.easybase.api.auth.dto.RolePermissionDto;
import com.easybase.api.auth.dto.mapper.RolePermissionMapper;
import com.easybase.core.auth.entity.ResourceAction;
import com.easybase.core.auth.entity.RolePermission;
import com.easybase.core.auth.service.ResourceActionLocalService;
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
	 * Check if a role has a specific permission.
	 * This endpoint is at root level since it doesn't require roleId in path.
	 *
	 * @param request the check permission request
	 * @return the check result
	 */
	@PostMapping("/check")
	public CheckPermissionResponse checkPermission(
		@RequestBody @Valid CheckPermissionRequest request) {

		String[] parts = request.getPermissionKey(
		).split(
			"\\.", 2
		);

		if (parts.length != 2) {
			throw new IllegalArgumentException(
				"Invalid permission key format. Expected: 'resourceType.action'");
		}

		String resourceType = parts[0];
		String actionKey = parts[1];

		ResourceAction action = _resourceActionLocalService.getResourceAction(
			resourceType, actionKey);

		if (action == null) {
			throw new IllegalArgumentException(
				"Unknown permission key: " + request.getPermissionKey());
		}

		boolean hasPermission = _rolePermissionService.hasPermission(
			request.getRoleId(), resourceType, action.getBitValue());

		return new CheckPermissionResponse(
			hasPermission, request.getPermissionKey());
	}

	/**
	 * Delete all permissions for a role.
	 *
	 * @param roleId the role ID
	 */
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllPermissionsForRole(@PathVariable UUID roleId) {
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
		@PathVariable UUID roleId, @PathVariable String resourceType) {

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
	public List<RolePermissionDto> getAllPermissions(
		@PathVariable UUID roleId) {

		List<RolePermission> permissions =
			_rolePermissionService.getPermissionsForRole(roleId);

		return _rolePermissionMapper.toDto(permissions);
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
		@PathVariable UUID roleId, @PathVariable String resourceType) {

		RolePermission permission =
			_rolePermissionService.getPermissionsForRoleAndResource(
				roleId, resourceType);

		if (permission == null) {
			return ResponseEntity.notFound(
			).build();
		}

		return ResponseEntity.ok(_rolePermissionMapper.toDto(permission));
	}

	/**
	 * Grant (add) specific permissions to a role for a resource type.
	 * Adds to existing permissions without removing any.
	 *
	 * @param roleId the role ID
	 * @param dto the grant permissions DTO
	 * @return the updated role permission
	 */
	@PostMapping(":grant")
	public RolePermissionDto grantPermissions(
		@PathVariable UUID roleId, @RequestBody @Valid RolePermissionDto dto) {

		if (!roleId.equals(dto.getRoleId())) {
			throw new IllegalArgumentException(
				"Role ID in path does not match role ID in request body");
		}

		int[] bitValues = _getBitValues(
			dto.getResourceType(), dto.getActions());

		RolePermission rolePermission = _rolePermissionService.addPermissions(
			roleId, dto.getResourceType(), bitValues);

		return _rolePermissionMapper.toDto(rolePermission);
	}

	/**
	 * Revoke (remove) specific permissions from a role for a resource type.
	 * Removes specified permissions while keeping others.
	 *
	 * @param roleId the role ID
	 * @param dto the revoke permissions DTO
	 * @return the updated role permission
	 */
	@PostMapping(":revoke")
	public ResponseEntity<RolePermissionDto> revokePermissions(
		@PathVariable UUID roleId, @RequestBody @Valid RolePermissionDto dto) {

		if (!roleId.equals(dto.getRoleId())) {
			throw new IllegalArgumentException(
				"Role ID in path does not match role ID in request body");
		}

		int[] bitValues = _getBitValues(
			dto.getResourceType(), dto.getActions());

		RolePermission rolePermission =
			_rolePermissionService.removePermissions(
				roleId, dto.getResourceType(), bitValues);

		if (rolePermission == null) {
			return ResponseEntity.notFound(
			).build();
		}

		return ResponseEntity.ok(_rolePermissionMapper.toDto(rolePermission));
	}

	/**
	 * Set (replace) all permissions for a role and resource type.
	 * Replaces all existing permissions with the new set.
	 *
	 * @param roleId the role ID
	 * @param resourceType the resource type
	 * @param dto the set permissions DTO
	 * @return the updated role permission
	 */
	@PutMapping("/{resourceType}")
	public RolePermissionDto setPermissions(
		@PathVariable UUID roleId, @PathVariable String resourceType,
		@RequestBody @Valid RolePermissionDto dto) {

		if (!roleId.equals(dto.getRoleId())) {
			throw new IllegalArgumentException(
				"Role ID in path does not match role ID in request body");
		}

		if (!resourceType.equals(dto.getResourceType())) {
			throw new IllegalArgumentException(
				"Resource type in path does not match resource type in request body");
		}

		long permissionsMask = 0L;

		if ((dto.getActions() != null) &&
			!dto.getActions(
			).isEmpty()) {

			int[] bitValues = _getBitValues(resourceType, dto.getActions());

			for (int bitValue : bitValues) {
				permissionsMask |= bitValue;
			}
		}

		RolePermission rolePermission =
			_rolePermissionService.createOrUpdateRolePermission(
				roleId, resourceType, permissionsMask);

		return _rolePermissionMapper.toDto(rolePermission);
	}

	/**
	 * Convert action keys to bit values by looking up ResourceActions.
	 *
	 * @param resourceType the resource type
	 * @param actionKeys list of action keys
	 * @return array of bit values
	 */
	private int[] _getBitValues(String resourceType, List<String> actionKeys) {
		List<Integer> bitValues = new ArrayList<>();

		for (String actionKey : actionKeys) {
			ResourceAction action =
				_resourceActionLocalService.getResourceAction(
					resourceType, actionKey);

			if (action == null) {
				throw new IllegalArgumentException(
					"Unknown action: " + resourceType + "." + actionKey);
			}

			if (!action.isActive()) {
				throw new IllegalArgumentException(
					"Action is not active: " + resourceType + "." + actionKey);
			}

			bitValues.add(action.getBitValue());
		}

		return bitValues.stream(
		).mapToInt(
			Integer::intValue
		).toArray();
	}

	private final ResourceActionLocalService _resourceActionLocalService;
	private final RolePermissionMapper _rolePermissionMapper;
	private final RolePermissionService _rolePermissionService;

}