/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.repository;

import com.easybase.core.auth.entity.RolePermission;
import com.easybase.infrastructure.data.repository.CompositeKeyBaseRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for RolePermission entity.
 *
 * @author Akhash R
 */
@Repository
public interface RolePermissionRepository
	extends CompositeKeyBaseRepository<RolePermission, RolePermission.RolePermissionId> {

	public void deleteByRoleId(UUID roleId);

	public void deleteByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

	public boolean existsByRoleIdAndPermissionId(
		UUID roleId, UUID permissionId);

	public List<RolePermission> findByPermissionId(UUID permissionId);

	public List<RolePermission> findByRoleId(UUID roleId);

	@Query("SELECT rp FROM RolePermission rp WHERE rp.roleId IN :roleIds")
	public List<RolePermission> findByRoleIdIn(
		@Param("roleIds") List<UUID> roleIds);

}