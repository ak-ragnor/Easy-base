/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Akhash R
 */
@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "eb_refresh_tokens")
public class RefreshTokenEntity {

	@Column(name = "expires_at", nullable = false)
	@NotNull
	private Instant expiresAt;

	@GeneratedValue(strategy = GenerationType.UUID)
	@Id
	private UUID id;

	@Column(name = "issued_at", nullable = false)
	@NotNull
	private Instant issuedAt;

	@Builder.Default
	@Column(name = "revoked", nullable = false)
	private boolean revoked = false;

	@Column(length = 36, name = "rotation_parent_id")
	private String rotationParentId;

	@Column(name = "session_id", nullable = false)
	@NotNull
	private UUID sessionId;

	@Column(name = "tenant_id", nullable = false)
	@NotNull
	private UUID tenantId;

	@Column(name = "user_id", nullable = false)
	@NotNull
	private UUID userId;

}