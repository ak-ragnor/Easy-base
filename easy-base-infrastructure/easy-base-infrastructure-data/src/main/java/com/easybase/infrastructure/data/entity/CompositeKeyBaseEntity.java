/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;

import java.util.UUID;

import lombok.Data;

/**
 * Base class for entities with composite keys.
 * Provides common audit fields but no single UUID id field.
 *
 * @author Akhash R
 */
@Data
@MappedSuperclass
public abstract class CompositeKeyBaseEntity {

	@PrePersist
	protected void onCreate() {
		createdAt = Instant.now();
		updatedAt = Instant.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = Instant.now();
	}

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "created_by")
	private UUID createdBy;

	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;

	@Column(name = "updated_at")
	private Instant updatedAt;

	@Column(name = "updated_by")
	private UUID updatedBy;

}