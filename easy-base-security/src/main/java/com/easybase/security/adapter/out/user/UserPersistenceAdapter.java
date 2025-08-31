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