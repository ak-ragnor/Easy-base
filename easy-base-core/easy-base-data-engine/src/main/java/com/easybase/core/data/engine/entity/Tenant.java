package com.easybase.core.data.engine.entity;

import java.util.ArrayList;
import java.util.List;

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
@ToString(exclude = "collections")
public class Tenant extends BaseEntity {

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<Collection> collections = new ArrayList<>();
}
