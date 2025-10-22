/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.service;

import com.easybase.core.role.entity.Role;
import com.easybase.core.role.entity.UserRole;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

/**
 * Local service interface for role business logic and data operations.
 * Contains all business logic, repository calls, and transaction management.
 * Does NOT perform permission checks - that's the responsibility of RoleService.
 *
 * @author Akhash R
 */
public interface RoleLocalService {

	/**
	 * Assigns a role to a user.
	 *
	 * @param userId the user ID
	 * @param roleId the role ID
	 * @param tenantId the tenant ID
	 * @param expiresAt the expiration time (can be null)
	 * @return the created UserRole
	 * @throws com.easybase.common.exception.ConflictException if user already has the role
	 */
	public UserRole assignRoleToUser(
		UUID userId, UUID roleId, UUID tenantId, Instant expiresAt);

	/**
	 * Creates a new role.
	 *
	 * @param name the role name
	 * @param description the role description
	 * @param tenantId the tenant ID (null for system roles)
	 * @param system whether this is a system role
	 * @return the created role
	 * @throws com.easybase.common.exception.ConflictException if role already exists
	 */
	public Role createRole(
		String name, String description, UUID tenantId, boolean system);

	/**
	 * Deletes a role.
	 *
	 * @param roleId the role ID
	 * @throws com.easybase.common.exception.ConflictException if trying to delete system role
	 * @throws com.easybase.common.exception.ResourceNotFoundException if role not found
	 */
	public void deleteRole(UUID roleId);

	/**
	 * Gets active role IDs for a user.
	 *
	 * @param userId the user ID
	 * @return list of active role IDs
	 */
	public List<UUID> getActiveRoleIdsByUserId(UUID userId);

	/**
	 * Gets active role IDs for a user in a specific tenant.
	 *
	 * @param userId the user ID
	 * @param tenantId the tenant ID
	 * @return list of active role IDs
	 */
	public List<UUID> getActiveRoleIdsByUserIdAndTenantId(
		UUID userId, UUID tenantId);

	/**
	 * Gets active user roles.
	 *
	 * @param userId the user ID
	 * @return list of active user roles
	 */
	public List<UserRole> getActiveUserRoles(UUID userId);

	/**
	 * Gets all available roles for a tenant (system + tenant-specific).
	 *
	 * @param tenantId the tenant ID
	 * @return list of available roles
	 */
	public List<Role> getAvailableRoles(UUID tenantId);

	/**
	 * Gets a role by ID.
	 *
	 * @param roleId the role ID
	 * @return the role
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	public Role getRoleById(UUID roleId);

	/**
	 * Gets a role by name.
	 *
	 * @param name the role name
	 * @param tenantId the tenant ID (null for system roles)
	 * @return the role
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	public Role getRoleByName(String name, UUID tenantId);

	/**
	 * Gets roles by their IDs.
	 *
	 * @param roleIds list of role IDs
	 * @return list of roles
	 */
	public List<Role> getRolesByIds(List<UUID> roleIds);

	/**
	 * Gets all system roles.
	 *
	 * @return list of system roles
	 */
	public List<Role> getSystemRoles();

	/**
	 * Gets tenant-specific roles.
	 *
	 * @param tenantId the tenant ID
	 * @return list of tenant roles
	 */
	public List<Role> getTenantRoles(UUID tenantId);

	/**
	 * Gets user authorities/role names.
	 *
	 * @param userId the user ID
	 * @return list of role names (authorities)
	 */
	public List<String> getUserAuthorities(UUID userId);

	/**
	 * Gets all roles assigned to a user.
	 *
	 * @param userId the user ID
	 * @return list of user roles
	 */
	public List<UserRole> getUserRoles(UUID userId);

	/**
	 * Gets roles assigned to a user in a specific tenant.
	 *
	 * @param userId the user ID
	 * @param tenantId the tenant ID
	 * @return list of user roles
	 */
	public List<UserRole> getUserRoles(UUID userId, UUID tenantId);

	/**
	 * Revokes a role from a user.
	 *
	 * @param userId the user ID
	 * @param roleId the role ID
	 */
	public void revokeRoleFromUser(UUID userId, UUID roleId);

	/**
	 * Updates a role's description.
	 *
	 * @param roleId the role ID
	 * @param description the new description
	 * @return the updated role
	 * @throws com.easybase.common.exception.ConflictException if trying to modify system role
	 * @throws com.easybase.common.exception.ResourceNotFoundException if role not found
	 */
	public Role updateRole(UUID roleId, String description);

}