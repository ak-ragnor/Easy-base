/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.entity;

import com.easybase.core.tenant.entity.Tenant;
import com.easybase.infrastructure.data.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Role entity representing both system and custom roles.
 * System roles are global, custom roles can be tenant-specific.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true, exclude = {"permissions", "userRoles"})
@NoArgsConstructor
@Table(
    name = "eb_roles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "tenant_id"})
    }
)
@ToString(exclude = {"permissions", "userRoles"})
public class Role extends BaseEntity {

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(max = 50)
    private String name;

    @Column(name = "description")
    @Size(max = 255)
    private String description;

    @Column(name = "is_system", nullable = false)
    private boolean isSystem = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @OneToMany(
        mappedBy = "role",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<RolePermission> permissions = new HashSet<>();

    @OneToMany(
        mappedBy = "role",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<UserRole> userRoles = new HashSet<>();

    public void addPermission(Permission permission) {
        RolePermission rolePermission = new RolePermission(this, permission);
        permissions.add(rolePermission);
    }

    public void removePermission(Permission permission) {
        permissions.removeIf(rp -> rp.getPermission().equals(permission));
    }
}