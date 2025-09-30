/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.entity;

import com.easybase.infrastructure.data.entity.CompositeKeyBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Join entity for many-to-many relationship between Role and Permission.
 * Uses only UUID references to avoid circular dependencies between modules.
 *
 * @author Akhash R
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@IdClass(RolePermission.RolePermissionId.class)
@NoArgsConstructor
@Table(name = "eb_role_permissions")
public class RolePermission extends CompositeKeyBaseEntity {

	public RolePermission(UUID roleId, UUID permissionId) {
		this.roleId = roleId;
		this.permissionId = permissionId;
	}

	@AllArgsConstructor
	@Data
	@NoArgsConstructor
	public static class RolePermissionId implements Serializable {

		private UUID permissionId;
		private UUID roleId;

	}

	@Column(name = "permission_id", nullable = false)
	@Id
	private UUID permissionId;

	@Column(name = "role_id", nullable = false)
	@Id
	private UUID roleId;

}