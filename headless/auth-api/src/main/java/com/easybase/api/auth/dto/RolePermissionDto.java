/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Consolidated DTO for RolePermission operations.
 * Contains roleId and a list of permissions for different resource types.
 *
 * Request Example (grant permissions):
 * {
 *   "roleId": "uuid",
 *   "permissions": [
 *     {
 *       "resourceType": "USER",
 *       "actions": ["create", "read", "update"]
 *     },
 *     {
 *       "resourceType": "PERMISSION",
 *       "actions": ["view", "manage"]
 *     }
 *   ]
 * }
 *
 * Response Example (get all permissions):
 * {
 *   "roleId": "uuid",
 *   "permissions": [
 *     {
 *       "resourceType": "USER",
 *       "actions": ["create", "read", "update", "delete"]
 *     },
 *     {
 *       "resourceType": "PERMISSION",
 *       "actions": ["view", "manage"]
 *     },
 *     {
 *       "resourceType": "DATA",
 *       "actions": ["read", "write"]
 *     }
 *   ]
 * }
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class RolePermissionDto {

	/**
	 * List of permissions for different resource types.
	 * Each PermissionDto represents permissions for one resource type.
	 */
	@NotEmpty
	@Valid
	private List<PermissionDto> permissions;

	/**
	 * The role ID for which permissions are being managed.
	 */
	@NotNull
	private UUID roleId;

}