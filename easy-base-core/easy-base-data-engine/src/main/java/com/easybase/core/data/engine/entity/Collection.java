package com.easybase.core.data.engine.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.easybase.common.data.entity.base.BaseEntity;
import com.easybase.core.tenant.entity.Tenant;

import lombok.*;

@Entity
@Table(name = "eb_collections", uniqueConstraints = {
		@UniqueConstraint(name = "uq_tenant_collection_name", columnNames = {
				"tenant_id", "name"})})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"tenant", "attributes"})
public class Collection extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_collection_tenant"))
	private Tenant tenant;

	@Column(name = "name", nullable = false, length = 63)
	private String name;

	@OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<Attribute> attributes = new ArrayList<>();

	public void addAttribute(Attribute attribute) {
		if (attribute == null) {
			throw new IllegalArgumentException("Attribute cannot be null");
		}
		attribute.setCollection(this);

		this.attributes.add(attribute);
	}

	public void removeAttribute(Attribute attribute) {
		this.attributes.remove(attribute);

		attribute.setCollection(null);
	}
}
