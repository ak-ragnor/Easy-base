package com.easybase.core.dataengine.entity;

import com.easybase.common.data.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eb_collections",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_tenant_collection_name", columnNames = {"tenant_id", "name"})
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"tenant", "attributes"})
public class Collection extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_collection_tenant"))
    private Tenant tenant;

    @Column(name = "name", nullable = false, length = 63)
    private String name;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Attribute> attributes = new ArrayList<>();
}
