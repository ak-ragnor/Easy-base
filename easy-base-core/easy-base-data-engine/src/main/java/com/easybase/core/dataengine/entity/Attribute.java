package com.easybase.core.dataengine.entity;

import com.easybase.common.data.entity.base.BaseEntity;
import com.easybase.core.dataengine.enums.AttributeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attributes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_collection_attribute_name", columnNames = {"collection_id", "name"})
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "collection")
public class Attribute extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_attribute_collection"))
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
