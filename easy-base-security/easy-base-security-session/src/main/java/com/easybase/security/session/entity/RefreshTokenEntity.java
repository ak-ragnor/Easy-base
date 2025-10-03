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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

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
			columnList = "tokenHash", name = "idx_refresh_token", unique = true
		),
		@Index(columnList = "sessionId", name = "idx_session_id"),
		@Index(columnList = "expiresAt", name = "idx_refresh_token_expires_at")
	},
	name = "refresh_tokens"
)
public class RefreshTokenEntity {

	@PreUpdate
	public void preUpdate() {
		if (Boolean.TRUE.equals(revoked) && (revokedAt == null)) {
			revokedAt = Instant.now();
		}
	}

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private Instant createdAt;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@Column(name = "revoked", nullable = false)
	private Boolean revoked = false;

	@Column(name = "revoked_at")
	private Instant revokedAt;

	@Column(name = "session_id", nullable = false)
	private String sessionId;

	@Column(name = "token_hash", nullable = false, unique = true)
	private String tokenHash;

}