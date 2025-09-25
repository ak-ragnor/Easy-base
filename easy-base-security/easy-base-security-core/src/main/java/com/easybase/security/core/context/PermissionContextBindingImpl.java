/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.core.context;

import com.easybase.context.api.domain.PermissionContext;
import com.easybase.context.api.port.PermissionContextProvider;
import com.easybase.context.api.port.PermissionProvider;
import com.easybase.core.auth.entity.Permission;
import com.easybase.core.auth.repository.PermissionRepository;
import com.easybase.core.role.service.RoleQueryService;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.core.service.PermissionContextBinding;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * Implementation of {@link PermissionContextBinding} that manages authentication
 * principal data in thread-local storage and provides service context creation.
 *
 * <p>This binding implementation ensures thread-safe storage of authentication
 * context and provides utilities for creating service contexts from principal data.</p>
 *
 * @author Akhash
 */
@Component
@RequiredArgsConstructor
public class PermissionContextBindingImpl implements PermissionContextBinding, PermissionContextProvider {

	@Override
	public void bind(AuthenticatedPrincipalData principal) {
		_principalHolder.set(principal);
	}

	@Override
	public void clear() {
		_principalHolder.remove();
	}

	@Override
	public AuthenticatedPrincipalData fromCurrentContext() {
		return _principalHolder.get();
	}

	@Override
	public PermissionContext fromPrincipal(AuthenticatedPrincipalData principal) {
		Set<String> permissions = _getUserPermissions(principal);

		return _permissionProvider.build(
			principal.getUserId(),
			principal.getTenantId(),
			permissions);
	}

	@Override
	public PermissionContext getCurrentPermissionContext() {
		AuthenticatedPrincipalData authenticatedPrincipalData = fromCurrentContext();

		if (authenticatedPrincipalData == null) {
			return null;
		}

		return fromPrincipal(authenticatedPrincipalData);
	}

	private final PermissionProvider _permissionProvider;
	private final PermissionRepository _permissionRepository;
	private final RoleQueryService _roleQueryService;
	private final ThreadLocal<AuthenticatedPrincipalData> _principalHolder =
		new ThreadLocal<>();

	/**
	 * Loads user permissions for the given principal.
	 *
	 * @param principal the authenticated principal data
	 * @return set of permission keys
	 */
	private Set<String> _getUserPermissions(AuthenticatedPrincipalData principal) {
		UUID userId = principal.getUserId();
		UUID tenantId = principal.getTenantId();

		List<UUID> roleIds = tenantId != null ?
			_roleQueryService.getActiveRoleIdsByUserIdAndTenantId(userId, tenantId) :
			_roleQueryService.getActiveRoleIdsByUserId(userId);

		if (roleIds.isEmpty()) {
			return Set.of();
		}

		List<Permission> userPermissions =
			_permissionRepository.findPermissionsByRoleIds(roleIds);

		return userPermissions.stream()
			.map(Permission::getPermissionKey)
			.collect(Collectors.toSet());
	}


}