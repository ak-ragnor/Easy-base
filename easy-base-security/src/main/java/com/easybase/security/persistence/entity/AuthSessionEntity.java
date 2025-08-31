/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.security.persistence.entity;

import com.easybase.common.data.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(
	indexes = {
		@Index(columnList = "session_token", name = "idx_session_token"),
		@Index(columnList = "user_id, tenant_id", name = "idx_user_tenant"),
		@Index(columnList = "expires_at", name = "idx_expires_at")
	},
	name = "eb_auth_sessions"
)
public class AuthSessionEntity extends BaseEntity {

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(length = 45, name = "ip_address")
	private String ipAddress;

	@Column(name = "revoked", nullable = false)
	private boolean revoked;

	@Column(
		length = 500, name = "session_token", nullable = false, unique = true
	)
	private String sessionToken;

	@Column(name = "tenant_id", nullable = false)
	private UUID tenantId;

	@Column(length = 1000, name = "user_agent")
	private String userAgent;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

}