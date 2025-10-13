/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Consolidated DTO for RolePermission operations using user-friendly action keys.
 * Handles grant, revoke, set, and read operations with a single DTO.
 *
 * @author Akhash R
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class RolePermissionDto {

	/**
	 * Validation group for grant operations.
	 */
	public interface Grant {
	}

	/**
	 * Validation group for revoke operations.
	 */
	public interface Revoke {
	}

	/**
	 * Validation group for set operations.
	 */
	public interface Set {
	}

	/**
	 * List of action keys for write operations (grant/revoke/set).
	 * Example: ["user.create", "user.read", "user.update"]
	 * WRITE_ONLY - only used in request bodies, never returned.
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotEmpty(groups = {Grant.class, Revoke.class})
	private List<@NotBlank String> actions;

	/**
	 * List of granted action keys for read operations.
	 * Example: ["user.create", "user.read"]
	 * READ_ONLY - only returned in responses, ignored in requests.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private List<String> grantedActions;

	@NotBlank
	@Size(max = 50)
	private String resourceType;

	@NotNull
	private UUID roleId;

}