/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.service;

import com.easybase.core.role.domain.entity.Role;
import com.easybase.core.role.domain.entity.UserRole;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

/**
 * External-facing service interface for role operations.
 * Performs permission checks before delegating to RoleLocalService.
 * Never performs persistence directly - always delegates to RoleLocalService.
 *
 * @author Akhash R
 */
public interface RoleService {

	/**
	 * Assigns a role to a user.
	 * Requires ROLE:ASSIGN permission.
	 *
	 * @param userId the user ID
	 * @param roleId the role ID
	 * @param tenantId the tenant ID
	 * @param expiresAt the expiration time (can be null)
	 * @return the created UserRole
	 * @throws com.easybase.common.exception.ConflictException if user already has the role
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public UserRole assignRoleToUser(
		UUID userId, UUID roleId, UUID tenantId, Instant expiresAt);

	/**
	 * Creates a new role.
	 * Requires ROLE:CREATE permission.
	 *
	 * @param name the role name
	 * @param description the role description
	 * @param tenantId the tenant ID (null for system roles)
	 * @param system whether this is a system role
	 * @return the created role
	 * @throws com.easybase.common.exception.ConflictException if role already exists
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public Role createRole(
		String name, String description, UUID tenantId, boolean system);

	/**
	 * Deletes a role.
	 * Requires ROLE:DELETE permission.
	 *
	 * @param roleId the role ID
	 * @throws com.easybase.common.exception.ConflictException if trying to delete system role
	 * @throws com.easybase.common.exception.ResourceNotFoundException if role not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public void deleteRole(UUID roleId);

	/**
	 * Gets all available roles for a tenant (system + tenant-specific).
	 * Requires ROLE:LIST permission.
	 *
	 * @param tenantId the tenant ID
	 * @return list of available roles
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public List<Role> getAvailableRoles(UUID tenantId);

	/**
	 * Gets a role by ID.
	 * Requires ROLE:VIEW permission.
	 *
	 * @param roleId the role ID
	 * @return the role
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public Role getRoleById(UUID roleId);

	/**
	 * Gets all roles assigned to a user.
	 * Requires ROLE:VIEW permission.
	 *
	 * @param userId the user ID
	 * @return list of user roles
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public List<UserRole> getUserRoles(UUID userId);

	/**
	 * Revokes a role from a user.
	 * Requires ROLE:REVOKE permission.
	 *
	 * @param userId the user ID
	 * @param roleId the role ID
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public void revokeRoleFromUser(UUID userId, UUID roleId);

	/**
	 * Updates a role's description.
	 * Requires ROLE:UPDATE permission.
	 *
	 * @param roleId the role ID
	 * @param description the new description
	 * @return the updated role
	 * @throws com.easybase.common.exception.ConflictException if trying to modify system role
	 * @throws com.easybase.common.exception.ResourceNotFoundException if role not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public Role updateRole(UUID roleId, String description);

}