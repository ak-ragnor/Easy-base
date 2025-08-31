/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.adapter.out.user;

import com.easybase.core.user.entity.User;
import com.easybase.core.user.entity.UserCredential;
import com.easybase.core.user.repository.UserCredentialRepository;
import com.easybase.core.user.repository.UserRepository;
import com.easybase.security.domain.port.out.LoadUserPort;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort {

	@Override
	public Optional<User> findByEmailAndTenantId(String email, UUID tenantId) {
		return _userRepository.findActiveByEmailAndTenantId(email, tenantId);
	}

	@Override
	public Optional<User> findById(UUID userId) {
		return _userRepository.findById(userId);
	}

	@Override
	public Optional<UserCredential> findCredentialByUserId(
		UUID userId, String credentialType) {

		return _userCredentialRepository.findByUserIdAndType(
			userId, credentialType);
	}

	@Override
	public boolean verifyPassword(String plainPassword, String hashedPassword) {
		return _passwordEncoder.matches(plainPassword, hashedPassword);
	}

	private final PasswordEncoder _passwordEncoder;
	private final UserCredentialRepository _userCredentialRepository;
	private final UserRepository _userRepository;

}