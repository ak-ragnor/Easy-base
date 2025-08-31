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

package com.easybase.common.data.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@MappedSuperclass
@Setter
@ToString
public abstract class BaseEntity {

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BaseEntity)) {
			return false;
		}

		BaseEntity that = (BaseEntity)o;

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
	protected String createdBy;

	@Column(name = "updated_by")
	protected String updatedBy;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

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
	private LocalDateTime updatedAt;

}