/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.service.impl;

import com.easybase.context.api.util.PermissionChecker;
import com.easybase.core.user.action.UserActions;
import com.easybase.core.user.domain.entity.User;
import com.easybase.core.user.domain.entity.UserCredential;
import com.easybase.core.user.service.UserLocalService;
import com.easybase.core.user.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserService}.
 * ALWAYS performs permission checks before delegating to UserLocalService.
 * Never performs persistence directly - always delegates to UserLocalService.
 *
 * <p>If permission checks are not needed, use UserLocalService directly.</p>
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	@Override
	public void addDefaultPasswordCredential(UUID userId) {
		_permissionChecker.check(UserActions.UPDATE);
		_userLocalService.addDefaultPasswordCredential(userId);
	}

	@Override
	public void addPasswordCredential(UUID userId, String plainPassword) {
		_permissionChecker.check(UserActions.UPDATE);
		_userLocalService.addPasswordCredential(userId, plainPassword);
	}

	@Override
	public UserCredential addUserCredential(
		UUID userId, String type, Map<String, Object> credentialData) {

		_permissionChecker.check(UserActions.UPDATE);

		return _userLocalService.addUserCredential(
			userId, type, credentialData);
	}

	@Override
	public User authenticateUser(String email, String password, UUID tenantId) {

		// No permission check - public authentication endpoint

		return _userLocalService.authenticateUser(email, password, tenantId);
	}

	@Override
	public User createUser(
		String email, String firstName, String lastName, String displayName,
		UUID tenantId) {

		_permissionChecker.check(UserActions.CREATE);

		return _userLocalService.createUser(
			email, firstName, lastName, displayName, tenantId);
	}

	@Override
	public void deleteUser(UUID id) {
		_permissionChecker.check(UserActions.DELETE);
		_userLocalService.deleteUser(id);
	}

	@Override
	public User getUser(String email, UUID tenantId) {
		_permissionChecker.check(UserActions.VIEW);

		return _userLocalService.getUser(email, tenantId);
	}

	@Override
	public User getUser(UUID id) {
		_permissionChecker.check(UserActions.VIEW);

		return _userLocalService.getUser(id);
	}

	@Override
	public UserCredential getUserCredential(
		UUID userId, String credentialType) {

		_permissionChecker.check(UserActions.VIEW);

		return _userLocalService.getUserCredential(userId, credentialType);
	}

	@Override
	public List<UserCredential> getUserCredentials(UUID userId) {
		_permissionChecker.check(UserActions.VIEW);

		return _userLocalService.getUserCredentials(userId);
	}

	@Override
	public List<User> getUsers(UUID tenantId) {
		_permissionChecker.check(UserActions.LIST);

		return _userLocalService.getUsers(tenantId);
	}

	@Override
	public void removeUserCredential(UUID userId, String credentialType) {
		_permissionChecker.check(UserActions.UPDATE);
		_userLocalService.removeUserCredential(userId, credentialType);
	}

	@Override
	public UserCredential updatePasswordCredential(
		UUID userId, String plainPassword) {

		_permissionChecker.check(UserActions.CHANGE_PASSWORD);

		return _userLocalService.updatePasswordCredential(
			userId, plainPassword);
	}

	@Override
	public User updateUser(
		UUID id, String firstName, String lastName, String displayName) {

		_permissionChecker.check(UserActions.UPDATE);

		return _userLocalService.updateUser(
			id, firstName, lastName, displayName);
	}

	private final PermissionChecker _permissionChecker;
	private final UserLocalService _userLocalService;

}