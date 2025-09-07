/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.adapter.out.persistence.entity;

import com.easybase.infrastructure.data.entity.BaseEntity;

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

/**
 * @author Akhash R
 */
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