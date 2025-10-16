/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.entity;

import com.easybase.core.auth.util.BitMaskUtil;
import com.easybase.infrastructure.data.entity.CompositeKeyBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Join entity mapping roles to resource permissions using bitmasks.
 * Each entry represents all permissions a role has for a specific resource type.
 * Permissions are stored as a bitmask for efficient permission checks.
 *
 * @author Akhash R
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@IdClass(RolePermission.RolePermissionId.class)
@NoArgsConstructor
@Table(name = "eb_role_permissions")
public class RolePermission extends CompositeKeyBaseEntity {

	public RolePermission(UUID roleId, String resourceType) {
		this.roleId = roleId;
		this.resourceType = resourceType;
		permissionsMask = 0L;
	}

	public RolePermission(
		UUID roleId, String resourceType, long permissionsMask) {

		this.roleId = roleId;
		this.resourceType = resourceType;
		this.permissionsMask = permissionsMask;
	}

	/**
	 * Add a permission to this role.
	 *
	 * @param bitValue the bit value of the permission to add
	 */
	public void addPermission(int bitValue) {
		this.permissionsMask = BitMaskUtil.addBit(this.permissionsMask, bitValue);
	}

	/**
	 * Add multiple permissions to this role.
	 *
	 * @param bitValues the bit values of the permissions to add
	 */
	public void addPermissions(int... bitValues) {
		this.permissionsMask = BitMaskUtil.addBits(this.permissionsMask, bitValues);
	}

	/**
	 * Clear all permissions.
	 */
	public void clearPermissions() {
		this.permissionsMask = BitMaskUtil.clearMask();
	}

	/**
	 * Remove a permission from this role.
	 *
	 * @param bitValue the bit value of the permission to remove
	 */
	public void removePermission(int bitValue) {
		this.permissionsMask = BitMaskUtil.removeBit(this.permissionsMask, bitValue);
	}

	/**
	 * Remove multiple permissions from this role.
	 *
	 * @param bitValues the bit values of the permissions to remove
	 */
	public void removePermissions(int... bitValues) {
		this.permissionsMask = BitMaskUtil.removeBits(this.permissionsMask, bitValues);
	}

	/**
	 * Set the permissions mask directly.
	 *
	 * @param permissionsMask the new permissions mask
	 */
	public void setPermissionsMask(long permissionsMask) {
		this.permissionsMask = permissionsMask;
	}

	@AllArgsConstructor
	@Data
	@NoArgsConstructor
	public static class RolePermissionId implements Serializable {

		private String resourceType;
		private UUID roleId;

	}

	@Column(name = "permissions_mask", nullable = false)
	private long permissionsMask = 0L;

	@Column(name = "resource_type", nullable = false)
	@Id
	@NotBlank
	@Size(max = 50)
	private String resourceType;

	@Column(name = "role_id", nullable = false)
	@Id
	private UUID roleId;

}