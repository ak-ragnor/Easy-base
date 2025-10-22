/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.service;

import com.easybase.core.user.entity.User;
import com.easybase.core.user.entity.UserCredential;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * External-facing service interface for user operations.
 * Performs permission checks before delegating to UserLocalService.
 * Never performs persistence directly - always delegates to UserLocalService.
 *
 * @author Akhash R
 */
public interface UserService {

	/**
	 * Adds default password credential for a user.
	 * Requires USER:UPDATE permission.
	 *
	 * @param userId the user ID
	 * @throws com.easybase.common.exception.ConflictException if password credential already exists
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public void addDefaultPasswordCredential(UUID userId);

	/**
	 * Adds password credential for a user.
	 * Requires USER:UPDATE permission.
	 *
	 * @param userId the user ID
	 * @param plainPassword the plain password
	 * @throws com.easybase.common.exception.ConflictException if password credential already exists
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public void addPasswordCredential(UUID userId, String plainPassword);

	/**
	 * Adds a user credential.
	 * Requires USER:UPDATE permission.
	 *
	 * @param userId the user ID
	 * @param type the credential type
	 * @param credentialData the credential data
	 * @return the created credential
	 * @throws com.easybase.common.exception.ConflictException if credential type already exists
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public UserCredential addUserCredential(
		UUID userId, String type, Map<String, Object> credentialData);

	/**
	 * Authenticates a user with email and password.
	 * No permission check - public authentication endpoint.
	 *
	 * @param email the user email
	 * @param password the password
	 * @param tenantId the tenant ID
	 * @return the authenticated user
	 * @throws com.easybase.common.exception.ResourceNotFoundException if authentication fails
	 */
	public User authenticateUser(String email, String password, UUID tenantId);

	/**
	 * Creates a new user.
	 * Requires USER:CREATE permission.
	 *
	 * @param email the email
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param displayName the display name
	 * @param tenantId the tenant ID
	 * @return the created user
	 * @throws com.easybase.common.exception.ConflictException if email already exists
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public User createUser(
		String email, String firstName, String lastName, String displayName,
		UUID tenantId);

	/**
	 * Soft deletes a user by ID.
	 * Requires USER:DELETE permission.
	 *
	 * @param id the user ID
	 * @throws com.easybase.common.exception.ResourceNotFoundException if user not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public void deleteUser(UUID id);

	/**
	 * Gets a user by email and tenant.
	 * Requires USER:VIEW permission.
	 *
	 * @param email the email
	 * @param tenantId the tenant ID
	 * @return the user
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public User getUser(String email, UUID tenantId);

	/**
	 * Gets a user by ID.
	 * Requires USER:VIEW permission.
	 *
	 * @param id the user ID
	 * @return the user
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public User getUser(UUID id);

	/**
	 * Gets a user credential by type.
	 * Requires USER:VIEW permission.
	 *
	 * @param userId the user ID
	 * @param credentialType the credential type
	 * @return the user credential
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public UserCredential getUserCredential(UUID userId, String credentialType);

	/**
	 * Gets all user credentials.
	 * Requires USER:VIEW permission.
	 *
	 * @param userId the user ID
	 * @return list of credentials
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public List<UserCredential> getUserCredentials(UUID userId);

	/**
	 * Gets all users for a tenant.
	 * Requires USER:LIST permission.
	 *
	 * @param tenantId the tenant ID
	 * @return list of users
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public List<User> getUsers(UUID tenantId);

	/**
	 * Removes a user credential.
	 * Requires USER:UPDATE permission.
	 *
	 * @param userId the user ID
	 * @param credentialType the credential type
	 * @throws com.easybase.common.exception.ResourceNotFoundException if credential not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public void removeUserCredential(UUID userId, String credentialType);

	/**
	 * Updates a password credential.
	 * Requires USER:CHANGE_PASSWORD permission.
	 *
	 * @param userId the user ID
	 * @param plainPassword the new password
	 * @return the updated credential
	 * @throws com.easybase.common.exception.ResourceNotFoundException if credential not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public UserCredential updatePasswordCredential(
		UUID userId, String plainPassword);

	/**
	 * Updates user information.
	 * Requires USER:UPDATE permission.
	 *
	 * @param id the user ID
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param displayName the display name
	 * @return the updated user
	 * @throws com.easybase.common.exception.ResourceNotFoundException if user not found
	 * @throws com.easybase.common.exception.ForbiddenException if permission denied
	 */
	public User updateUser(
		UUID id, String firstName, String lastName, String displayName);

}