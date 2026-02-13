/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.entity;

import com.easybase.core.data.engine.domain.enums.AttributeType;
import com.easybase.infrastructure.data.entity.SingleKeyBaseEntity;

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

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * @author Akhash R
 */
@AllArgsConstructor
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
public class Attribute extends SingleKeyBaseEntity {

	@JoinColumn(
		foreignKey = @ForeignKey(name = "fk_attribute_collection"),
		name = "collection_id", nullable = false
	)
	@ManyToOne(fetch = FetchType.LAZY)
	private Collection collection;

	@Column(columnDefinition = "jsonb", name = "config")
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Object> config;

	@Column(name = "data_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private AttributeType dataType;

	@Column(name = "is_indexed", nullable = false)
	private Boolean indexed = false;

	@Column(length = 63, name = "name", nullable = false)
	private String name;

}