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
import com.easybase.core.data.engine.enums.AttributeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
	name = "eb_attributes",
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {"collection_id", "name"},
			name = "uq_collection_attribute_name"
		)
	}
)
@ToString(exclude = "collection")
public class Attribute extends BaseEntity {

	@JoinColumn(
		foreignKey = @ForeignKey(name = "fk_attribute_collection"),
		name = "collection_id", nullable = false
	)
	@ManyToOne(fetch = FetchType.LAZY)
	private Collection collection;

	@Column(name = "data_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private AttributeType dataType;

	@Builder.Default
	@Column(name = "is_indexed", nullable = false)
	private Boolean indexed = false;

	@Column(length = 63, name = "name", nullable = false)
	private String name;

}