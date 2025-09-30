/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.entity;

import com.easybase.infrastructure.data.entity.SingleKeyBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Permission entity representing an individual permission.
 * Permissions are defined as resource + action combinations.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = true, exclude = "rolePermissions")
@NoArgsConstructor
@Table(
	name = "eb_permissions",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"resource_type", "action"})
	}
)
@ToString(exclude = "rolePermissions")
public class Permission extends SingleKeyBaseEntity {

	public Permission(String resourceType, String action, String description) {
		this.resourceType = resourceType;
		this.action = action;
		this.description = description;

		permissionKey = resourceType + ":" + action;
	}

	@Column(name = "action", nullable = false)
	@NotBlank
	@Size(max = 50)
	private String action;

	@Column(name = "description")
	@Size(max = 255)
	private String description;

	@Column(name = "permission_key", nullable = false, unique = true)
	@NotBlank
	@Size(max = 100)
	private String permissionKey;

	@Column(name = "resource_type", nullable = false)
	@NotBlank
	@Size(max = 50)
	private String resourceType;

	@OneToMany(mappedBy = "permission")
	private Set<RolePermission> rolePermissions = new HashSet<>();

}