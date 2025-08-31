/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.entity;

import com.easybase.common.data.entity.base.BaseEntity;
import com.easybase.core.tenant.entity.Tenant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Akhash R
 */
@AllArgsConstructor
@Builder
@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(
	name = "eb_collections",
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {"tenant_id", "name"},
			name = "uq_tenant_collection_name"
		)
	}
)
@ToString(exclude = {"tenant", "attributes"})
public class Collection extends BaseEntity {

	public void addAttribute(Attribute attribute) {
		if (attribute == null) {
			throw new IllegalArgumentException("Attribute cannot be null");
		}

		attribute.setCollection(this);

		attributes.add(attribute);
	}

	public void removeAttribute(Attribute attribute) {
		attributes.remove(attribute);

		attribute.setCollection(null);
	}

	@Builder.Default
	@OneToMany(
		cascade = CascadeType.ALL, fetch = FetchType.LAZY,
		mappedBy = "collection"
	)
	private List<Attribute> attributes = new ArrayList<>();

	@Column(length = 63, name = "name", nullable = false)
	private String name;

	@JoinColumn(
		foreignKey = @ForeignKey(name = "fk_collection_tenant"),
		name = "tenant_id", nullable = false
	)
	@ManyToOne(fetch = FetchType.LAZY)
	private Tenant tenant;

}