/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.entity;

import com.easybase.infrastructure.data.entity.SingleKeyBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Role entity representing both system and custom roles.
 * System roles are global, custom roles can be tenant-specific.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(
	name = "eb_roles",
	uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "tenant_id"})}
)
public class Role extends SingleKeyBaseEntity {

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	@Column(name = "description")
	@Size(max = 255)
	private String description;

	@Column(name = "name", nullable = false)
	@NotBlank
	@Size(max = 50)
	private String name;

	@Column(name = "is_system", nullable = false)
	private boolean system = false;

	@Column(name = "tenant_id")
	private UUID tenantId;

}