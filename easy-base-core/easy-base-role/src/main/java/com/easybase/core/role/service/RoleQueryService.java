/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.service;

import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.role.entity.Role;
import com.easybase.core.role.entity.UserRole;
import com.easybase.core.role.repository.RoleRepository;
import com.easybase.core.role.repository.UserRoleRepository;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Read-only service for role queries.
 * Used to avoid circular dependencies in permission checking.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RoleQueryService {

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

	private final RoleRepository _roleRepository;
	private final UserRoleRepository _userRoleRepository;

}