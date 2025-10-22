/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.time.Instant;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * @author Akhash R
 */
@Getter
@MappedSuperclass
@Setter
@ToString
public abstract class SingleKeyBaseEntity {

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof SingleKeyBaseEntity)) {
			return false;
		}

		SingleKeyBaseEntity that = (SingleKeyBaseEntity)o;

		if ((id != null) && id.equals(that.id)) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		if (id == null) {
			return 0;
		}

		return id.hashCode();
	}

	@Column(name = "created_by", updatable = false)
	protected UUID createdBy;

	@Column(name = "updated_by")
	protected UUID updatedBy;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private Instant createdAt;

	@Column(
		columnDefinition = "boolean default false", name = "is_deleted",
		nullable = false
	)
	private Boolean deleted = false;

	@Column(
		columnDefinition = "uuid", name = "id", nullable = false,
		updatable = false
	)
	@Id
	private UUID id = UUID.randomUUID();

	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private Instant updatedAt;

}