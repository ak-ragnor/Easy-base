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