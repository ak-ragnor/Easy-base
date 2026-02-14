/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.service;

import com.easybase.core.user.domain.entity.User;
import com.easybase.core.user.domain.entity.UserCredential;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Local service interface for user business logic and data operations.
 * Contains all business logic, repository calls, and transaction management.
 * Does NOT perform permission checks - that's the responsibility of UserService.
 *
 * @author Akhash R
 */
public interface UserLocalService {

	/**
	 * Adds default password credential for a user.
	 *
	 * @param userId the user ID
	 * @throws com.easybase.common.exception.ConflictException if password credential already exists
	 */
	public void addDefaultPasswordCredential(UUID userId);

	/**
	 * Adds password credential for a user.
	 *
	 * @param userId the user ID
	 * @param plainPassword the plain password
	 * @throws com.easybase.common.exception.ConflictException if password credential already exists
	 */
	public void addPasswordCredential(UUID userId, String plainPassword);

	/**
	 * Adds a user credential.
	 *
	 * @param userId the user ID
	 * @param type the credential type
	 * @param credentialData the credential data
	 * @return the created credential
	 * @throws com.easybase.common.exception.ConflictException if credential type already exists
	 */
	public UserCredential addUserCredential(
		UUID userId, String type, Map<String, Object> credentialData);

	/**
	 * Authenticates a user with email and password.
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
	 *
	 * @param email the email
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param displayName the display name
	 * @param tenantId the tenant ID
	 * @return the created user
	 * @throws com.easybase.common.exception.ConflictException if email already exists
	 */
	public User createUser(
		String email, String firstName, String lastName, String displayName,
		UUID tenantId);

	/**
	 * Soft deletes a user by ID.
	 *
	 * @param id the user ID
	 * @throws com.easybase.common.exception.ResourceNotFoundException if user not found
	 */
	public void deleteUser(UUID id);

	/**
	 * Gets a user by email and tenant.
	 *
	 * @param email the email
	 * @param tenantId the tenant ID
	 * @return the user
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	public User getUser(String email, UUID tenantId);

	/**
	 * Gets a user by ID.
	 *
	 * @param id the user ID
	 * @return the user
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	public User getUser(UUID id);

	/**
	 * Gets a user credential by type.
	 *
	 * @param userId the user ID
	 * @param credentialType the credential type
	 * @return the user credential
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	public UserCredential getUserCredential(UUID userId, String credentialType);

	/**
	 * Gets all user credentials.
	 *
	 * @param userId the user ID
	 * @return list of credentials
	 */
	public List<UserCredential> getUserCredentials(UUID userId);

	/**
	 * Gets all users for a tenant.
	 *
	 * @param tenantId the tenant ID
	 * @return list of users
	 */
	public List<User> getUsers(UUID tenantId);

	/**
	 * Removes a user credential.
	 *
	 * @param userId the user ID
	 * @param credentialType the credential type
	 * @throws com.easybase.common.exception.ResourceNotFoundException if credential not found
	 */
	public void removeUserCredential(UUID userId, String credentialType);

	/**
	 * Updates a password credential.
	 *
	 * @param userId the user ID
	 * @param plainPassword the new password
	 * @return the updated credential
	 * @throws com.easybase.common.exception.ResourceNotFoundException if credential not found
	 */
	public UserCredential updatePasswordCredential(
		UUID userId, String plainPassword);

	/**
	 * Updates user information.
	 *
	 * @param id the user ID
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param displayName the display name
	 * @return the updated user
	 * @throws com.easybase.common.exception.ResourceNotFoundException if user not found
	 */
	public User updateUser(
		UUID id, String firstName, String lastName, String displayName);

}