/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.entity;

import com.easybase.infrastructure.data.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity to define available actions for each resource type.
 * This allows dynamic configuration of what actions are available per resource.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(
    name = "eb_resource_actions",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"resource_type", "action_key"})
    }
)
public class ResourceAction extends BaseEntity {

    @Column(name = "resource_type", nullable = false)
    @NotBlank
    @Size(max = 50)
    private String resourceType;

    @Column(name = "action_key", nullable = false)
    @NotBlank
    @Size(max = 50)
    private String actionKey;

    @Column(name = "action_name", nullable = false)
    @NotBlank
    @Size(max = 100)
    private String actionName;

    @Column(name = "description")
    @Size(max = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}