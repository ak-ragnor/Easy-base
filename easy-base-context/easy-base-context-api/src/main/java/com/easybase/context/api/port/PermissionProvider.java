/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.port;

import com.easybase.context.api.domain.PermissionContext;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * SPI interface for building PermissionContext instances.
 * Implementations provide different permission resolution strategies
 * based on authentication method, environment, or testing needs.
 *
 * @author Akhash R
 */
public interface PermissionProvider {

	/**
	 * Builds a PermissionContext for the given user and tenant.
	 *
	 * @param userId the user ID
	 * @param tenantId the tenant ID
	 * @param permissions the set of permission keys
	 * @param roles the list of role names
	 * @return a fully constructed PermissionContext
	 */
	public PermissionContext build(
		UUID userId, UUID tenantId, Set<String> permissions,
		List<String> roles);

}