/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.auth.entity.Permission;
import com.easybase.core.auth.entity.Role;
import com.easybase.core.auth.entity.UserRole;
import com.easybase.core.auth.repository.RoleRepository;
import com.easybase.core.auth.repository.UserRoleRepository;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.user.entity.User;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing roles and role assignments.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Transactional
public class RoleService {

	@CacheEvict(
		allEntries = true,
		value = {"rolePermissions", "userPermissions", "userTenantPermissions"}
	)
	public void addPermissionToRole(UUID roleId, Permission permission) {
		Role role = getRoleById(roleId);

		role.addPermission(permission);

		roleRepository.save(role);
	}

	@CacheEvict(
		allEntries = true, value = {"userPermissions", "userTenantPermissions"}
	)
	public UserRole assignRoleToUser(
		User user, Role role, Tenant tenant, UUID assignedBy,
		Instant expiresAt) {

		if (userRoleRepository.existsByUserIdAndRoleId(
				user.getId(), role.getId())) {

			throw new ConflictException("User already has this role assigned");
		}

		UserRole userRole = new UserRole(user, role, tenant);

		userRole.setAssignedBy(assignedBy);
		userRole.setExpiresAt(expiresAt);

		return userRoleRepository.save(userRole);
	}

	public Role createRole(
		String name, String description, UUID tenantId, boolean isSystem) {

		if (isSystem && roleRepository.existsByNameAndIsSystemTrue(name)) {
			throw new ConflictException(
				"System role with name '" + name + "' already exists");
		}

		if (!isSystem && (tenantId != null) &&
			roleRepository.existsByNameAndTenantId(name, tenantId)) {

			throw new ConflictException(
				"Role with name '" + name + "' already exists for this tenant");
		}

		Role role = new Role();

		role.setName(name);
		role.setDescription(description);
		role.setSystem(isSystem);
		role.setActive(true);

		if (!isSystem && (tenantId != null)) {
			Tenant tenant = new Tenant();

			tenant.setId(tenantId);
			role.setTenant(tenant);
		}

		return roleRepository.save(role);
	}

	public int deactivateExpiredRoles() {
		return userRoleRepository.deactivateExpiredRoles(Instant.now());
	}

	public void deleteRole(UUID roleId) {
		Role role = getRoleById(roleId);

		if (role.isSystem()) {
			throw new ConflictException("Cannot delete system roles");
		}

		roleRepository.delete(role);
	}

	@Transactional(readOnly = true)
	public List<UserRole> getActiveUserRoles(UUID userId) {
		return userRoleRepository.findActiveUserRoles(userId, Instant.now());
	}

	@Transactional(readOnly = true)
	public List<Role> getAvailableRoles(UUID tenantId) {
		return roleRepository.findSystemAndTenantRoles(tenantId);
	}

	@Transactional(readOnly = true)
	public Role getRoleById(UUID roleId) {
		return roleRepository.findById(
			roleId
		).orElseThrow(
			() -> new ResourceNotFoundException(
				"Role not found with id: " + roleId)
		);
	}

	@Transactional(readOnly = true)
	public Role getRoleByName(String name, UUID tenantId) {
		if (tenantId != null) {
			return roleRepository.findByNameAndTenantId(
				name, tenantId
			).orElseThrow(
				() -> new ResourceNotFoundException("Role not found: " + name)
			);
		}

		return roleRepository.findByNameAndIsSystemTrue(
			name
		).orElseThrow(
			() -> new ResourceNotFoundException(
				"System role not found: " + name)
		);
	}

	@Cacheable(key = "#roleId", value = "rolePermissions")
	@Transactional(readOnly = true)
	public Role getRoleWithPermissions(UUID roleId) {
		return roleRepository.findByIdWithPermissions(
			roleId
		).orElseThrow(
			() -> new ResourceNotFoundException(
				"Role not found with id: " + roleId)
		);
	}

	@Transactional(readOnly = true)
	public List<Role> getSystemRoles() {
		return roleRepository.findByIsSystemTrue();
	}

	@Transactional(readOnly = true)
	public List<Role> getTenantRoles(UUID tenantId) {
		return roleRepository.findByTenantId(tenantId);
	}

	@Transactional(readOnly = true)
	public List<UserRole> getUserRoles(UUID userId) {
		return userRoleRepository.findByUserIdAndIsActiveTrue(userId);
	}

	@Transactional(readOnly = true)
	public List<UserRole> getUserRoles(UUID userId, UUID tenantId) {
		return userRoleRepository.findByUserIdAndTenantIdAndIsActiveTrue(
			userId, tenantId);
	}

	@CacheEvict(
		allEntries = true,
		value = {"rolePermissions", "userPermissions", "userTenantPermissions"}
	)
	public void removePermissionFromRole(UUID roleId, Permission permission) {
		Role role = getRoleById(roleId);

		role.removePermission(permission);

		roleRepository.save(role);
	}

	@CacheEvict(
		allEntries = true, value = {"userPermissions", "userTenantPermissions"}
	)
	public void revokeRoleFromUser(UUID userId, UUID roleId) {
		userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
	}

	@CacheEvict(key = "#roleId", value = "rolePermissions")
	public Role updateRole(UUID roleId, String description) {
		Role role = getRoleById(roleId);

		if (role.isSystem()) {
			throw new ConflictException("Cannot modify system roles");
		}

		role.setDescription(description);

		return roleRepository.save(role);
	}

	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;

}