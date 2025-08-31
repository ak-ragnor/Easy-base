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