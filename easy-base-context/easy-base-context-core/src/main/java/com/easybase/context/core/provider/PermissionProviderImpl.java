/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.provider;

import com.easybase.context.api.domain.PermissionContext;
import com.easybase.context.api.port.PermissionProvider;
import com.easybase.context.core.impl.PermissionContextImpl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * Implementation of {@link PermissionProvider} that provides utilities for creating permission contexts.
 *
 * <p>This provider implementation creates permission contexts from permission and role data
 * without managing any thread-local storage.</p>
 *
 * @author Akhash R
 */
@Component
public class PermissionProviderImpl implements PermissionProvider {

	@Override
	public PermissionContext build(
		UUID userId, UUID tenantId, Set<String> permissions,
		List<String> roles) {

		return PermissionContextImpl.builder(
		).userId(
			userId
		).tenantId(
			tenantId
		).permissions(
			permissions
		).roles(
			roles
		).build();
	}

}