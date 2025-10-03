/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.core.context;

import com.easybase.context.api.domain.PermissionContext;
import com.easybase.context.api.port.PermissionContextProvider;
import com.easybase.context.api.port.PermissionProvider;
import com.easybase.core.auth.entity.ResourceAction;
import com.easybase.core.auth.entity.RolePermission;
import com.easybase.core.auth.repository.ResourceActionRepository;
import com.easybase.core.auth.repository.RolePermissionRepository;
import com.easybase.core.role.entity.Role;
import com.easybase.core.role.service.RoleQueryService;
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
	implements PermissionContextBinding, PermissionContextProvider {

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
	public PermissionContext fromPrincipal(
		AuthenticatedPrincipalData principal) {

		Set<String> permissions = _getUserPermissions(principal);
		List<String> roles = _getUserRoles(principal);

		return _permissionProvider.build(
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
			return _roleQueryService.getActiveRoleIdsByUserIdAndTenantId(
				userId, tenantId);
		}

		return _roleQueryService.getActiveRoleIdsByUserId(userId);
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
			String resourceType = rolePerm.getResourceType();
			long permissionsMask = rolePerm.getPermissionsMask();

			List<ResourceAction> actions =
				_resourceActionRepository.findByResourceTypeAndActiveTrue(
					resourceType);

			for (ResourceAction action : actions) {
				if ((permissionsMask & action.getBitValue()) != 0) {
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

		List<Role> roles = _roleQueryService.getRolesByIds(
				roleIds
		);

		Stream<Role> roleStream = roles.stream(
		);

		return roleStream.map(
			role -> role.getName()
		).toList();
	}

	private final PermissionProvider _permissionProvider;
	private final ThreadLocal<AuthenticatedPrincipalData> _principalHolder =
		new ThreadLocal<>();
	private final ResourceActionRepository _resourceActionRepository;
	private final RolePermissionRepository _rolePermissionRepository;
	private final RoleQueryService _roleQueryService;

}