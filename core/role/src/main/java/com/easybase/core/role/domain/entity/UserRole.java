/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.domain.entity;

import com.easybase.infrastructure.data.entity.CompositeKeyBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Join entity for many-to-many relationship between User and Role.
 * Uses only UUID references to avoid dependencies on other modules.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@IdClass(UserRole.UserRoleId.class)
@NoArgsConstructor
@Table(name = "eb_user_roles")
public class UserRole extends CompositeKeyBaseEntity {

	public UserRole(UUID userId, UUID roleId, UUID tenantId) {
		this.userId = userId;
		this.roleId = roleId;
		this.tenantId = tenantId;
	}

	@AllArgsConstructor
	@Data
	@NoArgsConstructor
	public static class UserRoleId implements Serializable {

		private UUID roleId;
		private UUID userId;

	}

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	@Column(name = "expires_at")
	private Instant expiresAt;

	@Column(name = "role_id", nullable = false)
	@Id
	private UUID roleId;

	@Column(name = "tenant_id")
	private UUID tenantId;

	@Column(name = "user_id", nullable = false)
	@Id
	private UUID userId;

}