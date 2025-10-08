/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.service.impl;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.auth.repository.RolePermissionRepository;
import com.easybase.core.role.entity.Role;
import com.easybase.core.role.entity.UserRole;
import com.easybase.core.role.repository.RoleRepository;
import com.easybase.core.role.repository.UserRoleRepository;
import com.easybase.core.role.service.RoleLocalService;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link RoleLocalService}.
 * Contains all business logic, repository calls, and transaction management.
 * Does NOT perform permission checks.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Transactional
public class RoleLocalServiceImpl implements RoleLocalService {

	@CacheEvict(
		allEntries = true, value = {"userPermissions", "userTenantPermissions"}
	)
	public UserRole assignRoleToUser(
		UUID userId, UUID roleId, UUID tenantId, Instant expiresAt) {

		if (_userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
			throw new ConflictException("User already has this role assigned");
		}

		getRoleById(roleId);

		UserRole userRole = new UserRole(userId, roleId, tenantId);

		userRole.setExpiresAt(expiresAt);

		return _userRoleRepository.save(userRole);
	}

	public Role createRole(
		String name, String description, UUID tenantId, boolean system) {

		if (system && _roleRepository.existsByNameAndSystemTrue(name)) {
			throw new ConflictException(
				"System role with name '" + name + "' already exists");
		}

		if (!system && (tenantId != null) &&
			_roleRepository.existsByNameAndTenantId(name, tenantId)) {

			throw new ConflictException(
				"Role with name '" + name + "' already exists for this tenant");
		}

		Role role = new Role();

		role.setName(name);
		role.setDescription(description);
		role.setSystem(system);
		role.setActive(true);

		if (!system && (tenantId != null)) {
			role.setTenantId(tenantId);
		}

		return _roleRepository.save(role);
	}

	public void deleteRole(UUID roleId) {
		Role role = getRoleById(roleId);

		if (role.isSystem()) {
			throw new ConflictException("Cannot delete system roles");
		}

		_rolePermissionRepository.deleteByRoleId(roleId);

		_userRoleRepository.deleteByRoleId(roleId);

		_roleRepository.delete(role);
	}

	@Cacheable(key = "#userId", value = "userActiveRoleIds")
	@Transactional(readOnly = true)
	public List<UUID> getActiveRoleIdsByUserId(UUID userId) {
		return _userRoleRepository.findActiveRoleIdsByUserId(
			userId, Instant.now());
	}

	@Cacheable(
		key = "#userId + ':' + #tenantId", value = "userTenantActiveRoleIds"
	)
	@Transactional(readOnly = true)
	public List<UUID> getActiveRoleIdsByUserIdAndTenantId(
		UUID userId, UUID tenantId) {

		return _userRoleRepository.findActiveRoleIdsByUserIdAndTenantId(
			userId, tenantId, Instant.now());
	}

	@Transactional(readOnly = true)
	public List<UserRole> getActiveUserRoles(UUID userId) {
		return _userRoleRepository.findActiveUserRoles(userId, Instant.now());
	}

	@Transactional(readOnly = true)
	public List<Role> getAvailableRoles(UUID tenantId) {
		return _roleRepository.findSystemAndTenantRoles(tenantId);
	}

	@Transactional(readOnly = true)
	public List<String> getUserAuthorities(UUID userId) {
		List<UserRole> userRoles = getActiveUserRoles(userId);

		if (userRoles.isEmpty()) {
			return List.of();
		}

		List<UUID> roleIds = userRoles.stream()
			.map(UserRole::getRoleId)
			.toList();

		List<Role> roles = getRolesByIds(roleIds);

		return roles.stream()
			.map(Role::getName)
			.toList();
	}

	@Transactional(readOnly = true)
	public Role getRoleById(UUID roleId) {
		Optional<Role> roleOptional = _roleRepository.findById(roleId);

		return roleOptional.orElseThrow(
			() -> new ResourceNotFoundException(
				"Role not found with id: " + roleId));
	}

	@Transactional(readOnly = true)
	public Role getRoleByName(String name, UUID tenantId) {
		if (tenantId != null) {
			Optional<Role> roleOptional = _roleRepository.findByNameAndTenantId(
				name, tenantId);

			return roleOptional.orElseThrow(
				() -> new ResourceNotFoundException("Role not found: " + name));
		}

		Optional<Role> systemRoleOptional =
			_roleRepository.findByNameAndSystemTrue(name);

		return systemRoleOptional.orElseThrow(
			() -> new ResourceNotFoundException(
				"System role not found: " + name));
	}

	@Transactional(readOnly = true)
	public List<Role> getRolesByIds(List<UUID> roleIds) {
		return _roleRepository.findAllById(roleIds);
	}

	@Transactional(readOnly = true)
	public List<Role> getSystemRoles() {
		return _roleRepository.findBySystemTrue();
	}

	@Transactional(readOnly = true)
	public List<Role> getTenantRoles(UUID tenantId) {
		return _roleRepository.findByTenantId(tenantId);
	}

	@Transactional(readOnly = true)
	public List<UserRole> getUserRoles(UUID userId) {
		return _userRoleRepository.findByUserIdAndActiveTrue(userId);
	}

	@Transactional(readOnly = true)
	public List<UserRole> getUserRoles(UUID userId, UUID tenantId) {
		return _userRoleRepository.findByUserIdAndTenantIdAndActiveTrue(
			userId, tenantId);
	}

	@CacheEvict(
		allEntries = true, value = {"userPermissions", "userTenantPermissions"}
	)
	public void revokeRoleFromUser(UUID userId, UUID roleId) {
		_userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
	}

	public Role updateRole(UUID roleId, String description) {
		Role role = getRoleById(roleId);

		if (role.isSystem()) {
			throw new ConflictException("Cannot modify system roles");
		}

		role.setDescription(description);

		return _roleRepository.save(role);
	}

	private final RolePermissionRepository _rolePermissionRepository;
	private final RoleRepository _roleRepository;
	private final UserRoleRepository _userRoleRepository;

}
