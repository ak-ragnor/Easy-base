package com.easybase.core.tenant.entity;

import jakarta.persistence.*;

import com.easybase.common.data.entity.base.BaseEntity;

import lombok.*;

@Entity
@Table(name = "eb_tenants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Tenant extends BaseEntity {

	@Column(name = "name", nullable = false, length = 255)
	private String name;
}
