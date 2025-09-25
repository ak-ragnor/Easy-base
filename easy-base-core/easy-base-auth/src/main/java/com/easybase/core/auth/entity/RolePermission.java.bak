/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.io.Serializable;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Join entity for many-to-many relationship between Role and Permission.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@Entity
@IdClass(RolePermission.RolePermissionId.class)
@NoArgsConstructor
@Table(name = "eb_role_permissions")
public class RolePermission {

	public RolePermission(Role role, Permission permission) {
		this.role = role;
		this.permission = permission;

		this.grantedAt = Instant.now();
	}

	@AllArgsConstructor
	@Data
	@NoArgsConstructor
	public static class RolePermissionId implements Serializable {

		private UUID permission;
		private UUID role;

	}

	@Column(name = "granted_at", nullable = false)
	private Instant grantedAt = Instant.now();

	@Column(name = "granted_by")
	private UUID grantedBy;

	@Id
	@JoinColumn(name = "permission_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Permission permission;

	@Id
	@JoinColumn(name = "role_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Role role;

}