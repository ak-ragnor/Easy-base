/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.infrastructure.presistence.repository;

import com.easybase.core.auth.domain.entity.RolePermission;
import com.easybase.infrastructure.data.repository.CompositeKeyBaseRepository;

import java.util.List;
import java.util.Optional;
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
	extends CompositeKeyBaseRepository
		<RolePermission, RolePermission.RolePermissionId> {

	public void deleteByRoleId(UUID roleId);

	public void deleteByRoleIdAndResourceType(UUID roleId, String resourceType);

	public List<RolePermission> findByResourceType(String resourceType);

	public List<RolePermission> findByRoleId(UUID roleId);

	public Optional<RolePermission> findByRoleIdAndResourceType(
		UUID roleId, String resourceType);

	@Query("SELECT rp FROM RolePermission rp WHERE rp.roleId IN :roleIds")
	public List<RolePermission> findByRoleIdIn(
		@Param("roleIds") List<UUID> roleIds);

	@Query(
		"SELECT rp FROM RolePermission rp WHERE rp.roleId IN :roleIds AND rp.resourceType = :resourceType"
	)
	public List<RolePermission> findByRoleIdInAndResourceType(
		@Param("roleIds") List<UUID> roleIds,
		@Param("resourceType") String resourceType);

}