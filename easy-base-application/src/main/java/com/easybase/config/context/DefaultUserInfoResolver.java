/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.config.context;

import com.easybase.common.api.context.AbstractDefaultResolver;
import com.easybase.common.api.context.UserInfo;
import com.easybase.common.api.context.UserInfoResolver;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * Default implementation of UserInfoResolver that resolves user information
 * from the database using UserRepository. Uses AbstractDefaultResolver template
 * to eliminate code duplication.
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultUserInfoResolver
	extends AbstractDefaultResolver<UUID, UserInfo, User>
	implements UserInfoResolver {

	@Override
	public UserInfo resolve(UUID userId) {
		return super.resolve(userId);
	}

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
	protected UserInfo mapEntityToInfo(User user) {
		UUID id = user.getId();
		String email = user.getEmail();
		boolean active = isEntityActive(user);

		return new UserInfo(
			id, email, active, user::getDisplayName, () -> _extractRoles(user),
			() -> _extractScopes(user));
	}

	private List<String> _extractRoles(User user) {
		log.debug("Extracting roles for user: {}", user.getId());

		// TODO: Implement role extraction

		return List.of();
	}

	private List<String> _extractScopes(User user) {
		log.debug("Extracting scopes for user: {}", user.getId());

		// TODO: Implement scope extraction

		return List.of();
	}

	private final UserRepository _userRepository;

}