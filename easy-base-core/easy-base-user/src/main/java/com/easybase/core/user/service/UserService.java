/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.core.user.service;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.tenant.repository.TenantRepository;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.entity.UserCredential;
import com.easybase.core.user.repository.UserCredentialRepository;
import com.easybase.core.user.repository.UserRepository;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

	@Transactional
	public void addDefaultPasswordCredential(UUID userId) {
		addPasswordCredential(userId, _userDefaultPassword);
	}

	@Transactional
	public void addPasswordCredential(UUID userId, String plainPassword) {
		Optional<UserCredential> existingCredential =
			_userCredentialRepository.findByUserIdAndType(userId, "PASSWORD");

		if (existingCredential.isPresent()) {
			throw new ConflictException("UserCredential", "type", "PASSWORD");
		}

		String passwordHash = _passwordEncoder.encode(plainPassword);

		UserCredential credential = UserCredential.builder(
		).user(
			_getUser(userId)
		).passwordType(
			"PASSWORD"
		).passwordHash(
			passwordHash
		).passwordAlgo(
			"bcrypt"
		).passwordChangedAt(
			LocalDateTime.now()
		).build();

		_userCredentialRepository.save(credential);

		log.info("Added password credential userId={}", userId);
	}

	@Transactional
	public UserCredential addUserCredential(
		UUID userId, String type, Map<String, Object> credentialData) {

		Optional<UserCredential> existingCredential =
			_userCredentialRepository.findByUserIdAndType(userId, type);

		if (existingCredential.isPresent()) {
			throw new ConflictException("UserCredential", "type", type);
		}

		UserCredential credential = UserCredential.builder(
		).user(
			_getUser(userId)
		).passwordType(
			type
		).credentialData(
			credentialData
		).build();

		credential = _userCredentialRepository.save(credential);

		log.info("Added credential type={} userId={}", type, userId);

		return credential;
	}

	@Transactional
	public User createUser(
		String email, String firstName, String lastName, String displayName,
		UUID tenantId) {

		_validateUserEmail(email, tenantId);

		User user = User.builder(
		).email(
			email
		).firstName(
			firstName
		).lastName(
			lastName
		).displayName(
			displayName
		).tenant(
			_getTenant(tenantId)
		).build();

		user = _userRepository.save(user);

		addDefaultPasswordCredential(user.getId());

		log.info(
			"Created user with email={} id={} tenant={}", email, user.getId(),
			tenantId);

		return user;
	}

	@Transactional
	public void deleteUser(UUID id) {
		User user = _getUser(id);

		user.setDeleted(true);

		_userRepository.save(user);

		log.info("Soft deleted user id={}", id);
	}

	@Transactional(readOnly = true)
	public User getUser(String email, UUID tenantId) {
		Optional<User> userOptional =
			_userRepository.findActiveByEmailAndTenantId(email, tenantId);

		if (userOptional.isEmpty()) {
			throw new ResourceNotFoundException("User", "email", email);
		}

		return userOptional.get();
	}

	@Transactional(readOnly = true)
	public User getUser(UUID id) {
		return _getUser(id);
	}

	@Transactional(readOnly = true)
	public UserCredential getUserCredential(
		UUID userId, String credentialType) {

		Optional<UserCredential> credentialOptional =
			_userCredentialRepository.findByUserIdAndType(
				userId, credentialType);

		if (credentialOptional.isEmpty()) {
			throw new ResourceNotFoundException(
				"UserCredential", "type", credentialType);
		}

		return credentialOptional.get();
	}

	@Transactional(readOnly = true)
	public List<UserCredential> getUserCredentials(UUID userId) {
		return _userCredentialRepository.findByUserId(userId);
	}

	@Transactional(readOnly = true)
	public List<User> getUsers(UUID tenantId) {
		return _userRepository.findActiveByTenantId(tenantId);
	}

	@Transactional
	public void removeUserCredential(UUID userId, String credentialType) {
		Optional<UserCredential> credentialOptional =
			_userCredentialRepository.findByUserIdAndType(
				userId, credentialType);

		if (credentialOptional.isEmpty()) {
			throw new ResourceNotFoundException(
				"UserCredential", "type", credentialType);
		}

		_userCredentialRepository.delete(credentialOptional.get());
		log.info(
			"Removed credential type={} userId={}", credentialType, userId);
	}

	@Transactional
	public UserCredential updatePasswordCredential(
		UUID userId, String plainPassword) {

		Optional<UserCredential> credentialOptional =
			_userCredentialRepository.findByUserIdAndType(userId, "PASSWORD");

		if (credentialOptional.isEmpty()) {
			throw new ResourceNotFoundException(
				"UserCredential", "type", "PASSWORD");
		}

		UserCredential credential = credentialOptional.get();

		credential.setPasswordHash(_passwordEncoder.encode(plainPassword));
		credential.setPasswordAlgo("bcrypt");
		credential.setPasswordChangedAt(LocalDateTime.now());

		credential = _userCredentialRepository.save(credential);

		log.info("Updated password credential userId={}", userId);

		return credential;
	}

	@Transactional
	public User updateUser(
		UUID id, String firstName, String lastName, String displayName) {

		User user = _getUser(id);

		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setDisplayName(displayName);

		user = _userRepository.save(user);

		log.info("Updated user id={}", id);

		return user;
	}

	private Tenant _getTenant(UUID tenantId) {
		Optional<Tenant> tenantOptional = _tenantRepository.findById(tenantId);

		if (tenantOptional.isEmpty()) {
			throw new ResourceNotFoundException("Tenant", "id", tenantId);
		}

		return tenantOptional.get();
	}

	private User _getUser(UUID id) {
		Optional<User> userOptional = _userRepository.findById(id);

		if (userOptional.isEmpty()) {
			throw new ResourceNotFoundException("User", "id", id);
		}

		return userOptional.get();
	}

	private void _validateUserEmail(String email, UUID tenantId) {
		boolean exists = _userRepository.existsByEmailAndTenantId(
			email, tenantId);

		if (exists) {
			throw new ConflictException("User", "email", email);
		}
	}

	private final PasswordEncoder _passwordEncoder;
	private final TenantRepository _tenantRepository;
	private final UserCredentialRepository _userCredentialRepository;

	@Value("${easy-base.user.default-password:backToDust}")
	private String _userDefaultPassword;

	private final UserRepository _userRepository;

}