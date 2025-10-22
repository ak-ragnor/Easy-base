/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.session.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * @author Akhash
 */
@AllArgsConstructor
@Data
@Entity
@NoArgsConstructor
@Table(
	indexes = {
		@Index(
			columnList = "sessionId", name = "idx_session_id", unique = true
		),
		@Index(columnList = "userId, tenantId", name = "idx_user_tenant"),
		@Index(columnList = "expiresAt", name = "idx_expires_at"),
		@Index(columnList = "revoked", name = "idx_revoked")
	},
	name = "auth_sessions"
)
public class SessionEntity {

	@PreUpdate
	public void preUpdate() {
		if (Boolean.TRUE.equals(revoked) && (revokedAt == null)) {
			revokedAt = Instant.now();
		}
	}

	@Column(length = 45, name = "client_ip")
	private String clientIp;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private Instant createdAt;

	@Column(length = 500, name = "device_info")
	private String deviceInfo;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@Column(name = "last_accessed_at")
	@UpdateTimestamp
	private Instant lastAccessedAt;

	@Column(columnDefinition = "TEXT", name = "metadata")
	private String metadata;

	@Column(name = "revoked", nullable = false)
	private Boolean revoked = false;

	@Column(name = "revoked_at")
	private Instant revokedAt;

	@Column(name = "session_id", nullable = false, unique = true)
	private String sessionId;

	@Column(name = "tenant_id", nullable = false)
	private UUID tenantId;

	@Column(length = 500, name = "user_agent")
	private String userAgent;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

}