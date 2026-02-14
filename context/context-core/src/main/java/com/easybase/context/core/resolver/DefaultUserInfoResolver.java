/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.resolver;

import com.easybase.common.util.ListUtil;
import com.easybase.context.api.domain.UserInfo;
import com.easybase.context.api.port.AbstractDefaultResolver;
import com.easybase.context.api.port.UserInfoResolver;
import com.easybase.core.auth.domain.entity.ResourceAction;
import com.easybase.core.auth.domain.entity.RolePermission;
import com.easybase.core.auth.service.ResourceActionLocalService;
import com.easybase.core.auth.service.RolePermissionLocalService;
import com.easybase.core.auth.service.util.BitMaskUtil;
import com.easybase.core.role.service.RoleLocalService;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.service.TenantLocalService;
import com.easybase.core.user.domain.entity.User;
import com.easybase.core.user.infrastructure.presistence.repository.UserRepository;
import com.easybase.core.user.service.UserLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link UserInfoResolver} that resolves user information
 * from the database using UserRepository. Uses AbstractDefaultResolver template
 * to eliminate code duplication.
 *
 * <p>This resolver provides lazy loading of user roles and scopes and returns
 * the guest user when no user data is found.</p>
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class DefaultUserInfoResolver
	extends AbstractDefaultResolver<UUID, UserInfo, User>
	implements UserInfoResolver {

	@Override
	protected UserInfo createAnonymousInstance() {
		Tenant tenant = _tenantLocalService.getDefaultTenant();

		return toInfo(_userLocalService.getUser(_guestEmail, tenant.getId()));
	}

	@Override
	protected String getEntityType() {
		return "User";
	}

	@Override
	protected CrudRepository<User, UUID> getRepository() {
		return _userRepository;
	}

	@Override
	protected UserInfo toInfo(User user) {
		UUID id = user.getId();
		String email = user.getEmail();
		boolean active = isEntityActive(user);

		return new UserInfo(
			id, email, active, user::getDisplayName, () -> _extractRoles(user),
			() -> _extractScopes(user));
	}

	private List<String> _extractRoles(User user) {
		return _roleLocalService.getUserAuthorities(user.getId());
	}

	private List<String> _extractScopes(User user) {
		List<UUID> roleIds = _roleLocalService.getActiveRoleIdsByUserId(
			user.getId());

		if (ListUtil.isEmpty(roleIds)) {
			return List.of();
		}

		List<RolePermission> rolePermissions =
			_rolePermissionLocalService.getPermissionsForRoles(roleIds);

		List<String> scopes = new ArrayList<>();

		for (RolePermission rolePermission : rolePermissions) {
			List<ResourceAction> resourceActions =
				_resourceActionLocalService.getActiveResourceActions(
					rolePermission.getResourceType());

			for (ResourceAction resourceAction : resourceActions) {
				if (BitMaskUtil.hasBit(
						rolePermission.getPermissionsMask(),
						resourceAction.getBitValue())) {

					scopes.add(resourceAction.getActionKey());
				}
			}
		}

		return scopes;
	}

	@Value("${easy-base.guest.email:guest@easybase.com}")
	private String _guestEmail;

	private final ResourceActionLocalService _resourceActionLocalService;
	private final RoleLocalService _roleLocalService;
	private final RolePermissionLocalService _rolePermissionLocalService;
	private final TenantLocalService _tenantLocalService;
	private final UserLocalService _userLocalService;
	private final UserRepository _userRepository;

}