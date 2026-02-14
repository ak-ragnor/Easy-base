/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.core.context;

import com.easybase.context.api.domain.PermissionContext;
import com.easybase.context.api.port.PermissionContextProvider;
import com.easybase.core.auth.domain.entity.ResourceAction;
import com.easybase.core.auth.domain.entity.RolePermission;
import com.easybase.core.auth.infrastructure.presistence.repository.ResourceActionRepository;
import com.easybase.core.auth.infrastructure.presistence.repository.RolePermissionRepository;
import com.easybase.core.role.domain.entity.Role;
import com.easybase.core.role.service.RoleLocalService;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.core.service.PermissionContextBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

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
public class PermissionContextBindingImpl
	extends AbstractContextBinding implements PermissionContextBinding {

	@Override
	public PermissionContext fromPrincipal(
		AuthenticatedPrincipalData principal) {

		Set<String> permissions = _getUserPermissions(principal);
		List<String> roles = _getUserRoles(principal);

		return _permissionContextProvider.build(
			principal.getUserId(), principal.getTenantId(), permissions, roles);
	}

	@Override
	public PermissionContext getCurrentPermissionContext() {
		AuthenticatedPrincipalData authenticatedPrincipalData =
			fromCurrentContext();

		if (authenticatedPrincipalData == null) {
			return null;
		}

		return fromPrincipal(authenticatedPrincipalData);
	}

	/**
	 * Gets role IDs for the given principal.
	 *
	 * @param principal the authenticated principal data
	 * @return list of role IDs
	 */
	private List<UUID> _getRoleIds(AuthenticatedPrincipalData principal) {
		UUID userId = principal.getUserId();
		UUID tenantId = principal.getTenantId();

		if (tenantId != null) {
			return _roleLocalService.getActiveRoleIdsByUserIdAndTenantId(
				userId, tenantId);
		}

		return _roleLocalService.getActiveRoleIdsByUserId(userId);
	}

	/**
	 * Loads user permissions for the given principal using bitwise operations.
	 *
	 * @param principal the authenticated principal data
	 * @return set of permission keys
	 */
	private Set<String> _getUserPermissions(
		AuthenticatedPrincipalData principal) {

		List<UUID> roleIds = _getRoleIds(principal);

		if (roleIds.isEmpty()) {
			return Set.of();
		}

		List<RolePermission> rolePermissions =
			_rolePermissionRepository.findByRoleIdIn(roleIds);

		Set<String> permissionKeys = new HashSet<>();

		for (RolePermission rolePerm : rolePermissions) {
			List<ResourceAction> actions =
				_resourceActionRepository.findByResourceTypeAndActiveTrue(
					rolePerm.getResourceType());

			for (ResourceAction action : actions) {
				if ((rolePerm.getPermissionsMask() & action.getBitValue()) !=
						0) {

					permissionKeys.add(action.getActionKey());
				}
			}
		}

		return permissionKeys;
	}

	/**
	 * Loads user roles for the given principal.
	 *
	 * @param principal the authenticated principal data
	 * @return list of role names
	 */
	private List<String> _getUserRoles(AuthenticatedPrincipalData principal) {
		List<UUID> roleIds = _getRoleIds(principal);

		if (roleIds.isEmpty()) {
			return List.of();
		}

		List<Role> roles = _roleLocalService.getRolesByIds(roleIds);

		Stream<Role> rolesStream = roles.stream();

		return rolesStream.map(
			role -> role.getName()
		).toList();
	}

	private final PermissionContextProvider _permissionContextProvider;
	private final ResourceActionRepository _resourceActionRepository;
	private final RoleLocalService _roleLocalService;
	private final RolePermissionRepository _rolePermissionRepository;

}