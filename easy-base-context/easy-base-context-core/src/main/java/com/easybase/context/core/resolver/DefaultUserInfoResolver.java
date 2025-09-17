/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.resolver;

import com.easybase.context.api.domain.UserInfo;
import com.easybase.context.api.port.AbstractDefaultResolver;
import com.easybase.context.api.port.UserInfoResolver;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link UserInfoResolver} that resolves user information
 * from the database using UserRepository. Uses AbstractDefaultResolver template
 * to eliminate code duplication.
 *
 * <p>This resolver provides lazy loading of user roles and scopes and handles
 * anonymous users when no user data is found.</p>
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
		return UserInfo.anonymous();
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

		// TODO: Implement role extraction

		return List.of();
	}

	private List<String> _extractScopes(User user) {

		// TODO: Implement scope extraction

		return List.of();
	}

	private final UserRepository _userRepository;

}