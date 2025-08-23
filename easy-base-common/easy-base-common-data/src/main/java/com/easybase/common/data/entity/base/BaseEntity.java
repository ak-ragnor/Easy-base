package com.easybase.common.data.entity.base;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class BaseEntity {

	@Id
	@Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
	private UUID id = UUID.randomUUID();

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "created_by", updatable = false)
	protected String createdBy;

	@Column(name = "updated_by")
	protected String updatedBy;

	@Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
	private Boolean isDeleted = false;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BaseEntity that)) {
			return false;
		}

		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode() {
		if (id == null) {
			return 0;
		}

		return id.hashCode();
	}
}
