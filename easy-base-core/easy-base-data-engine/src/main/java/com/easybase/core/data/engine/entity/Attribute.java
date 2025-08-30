package com.easybase.core.data.engine.entity;

import jakarta.persistence.*;

import com.easybase.common.data.entity.base.BaseEntity;
import com.easybase.core.data.engine.enums.AttributeType;

import lombok.*;

@Entity
@Table(name = "eb_attributes", uniqueConstraints = {
		@UniqueConstraint(name = "uq_collection_attribute_name", columnNames = {
				"collection_id", "name" }) })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "collection")
public class Attribute extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "collection_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attribute_collection"))
	private Collection collection;

	@Column(name = "name", nullable = false, length = 63)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "data_type", nullable = false)
	private AttributeType dataType;

	@Column(name = "is_indexed", nullable = false)
	@Builder.Default
	private Boolean isIndexed = false;
}
