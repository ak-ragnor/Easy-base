/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.auth.entity.Permission;
import com.easybase.core.auth.repository.PermissionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing permissions.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Transactional
public class PermissionService {

	public Permission createPermission(
		String resourceType, String action, String description) {

		String permissionKey = resourceType + ":" + action;

		if (_permissionRepository.existsByPermissionKey(permissionKey)) {
			throw new ConflictException(
				"Permission already exists: " + permissionKey);
		}

		Permission permission = new Permission(
			resourceType, action, description);

		return _permissionRepository.save(permission);
	}

	public void deletePermission(UUID permissionId) {
		Permission permission = getPermissionById(permissionId);

		_permissionRepository.delete(permission);
	}

	@Transactional(readOnly = true)
	public List<Permission> getAllPermissions() {
		return _permissionRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Permission getPermissionById(UUID permissionId) {
		Optional<Permission> permissionOptional =
			_permissionRepository.findById(permissionId);

		return permissionOptional.orElseThrow(
			() -> new ResourceNotFoundException(
				"Permission not found with id: " + permissionId));
	}

	@Transactional(readOnly = true)
	public Permission getPermissionByKey(String permissionKey) {
		Optional<Permission> permissionOptional =
			_permissionRepository.findByPermissionKey(permissionKey);

		return permissionOptional.orElseThrow(
			() -> new ResourceNotFoundException(
				"Permission not found: " + permissionKey));
	}

	@Transactional(readOnly = true)
	public Permission getPermissionByResourceAndAction(
		String resourceType, String action) {

		Optional<Permission> permissionOptional =
			_permissionRepository.findByResourceTypeAndAction(
				resourceType, action);

		return permissionOptional.orElseThrow(
			() -> new ResourceNotFoundException(
				"Permission not found for resource: " + resourceType +
					", action: " + action));
	}

	@Transactional(readOnly = true)
	public List<Permission> getPermissionsByResource(String resourceType) {
		return _permissionRepository.findByResourceType(resourceType);
	}

	@Transactional(readOnly = true)
	public boolean permissionExists(String permissionKey) {
		return _permissionRepository.existsByPermissionKey(permissionKey);
	}

	@Transactional(readOnly = true)
	public boolean permissionExists(String resourceType, String action) {
		return _permissionRepository.existsByResourceTypeAndAction(
			resourceType, action);
	}

	public Permission updatePermission(UUID permissionId, String description) {
		Permission permission = getPermissionById(permissionId);

		permission.setDescription(description);

		return _permissionRepository.save(permission);
	}

	private final PermissionRepository _permissionRepository;

}