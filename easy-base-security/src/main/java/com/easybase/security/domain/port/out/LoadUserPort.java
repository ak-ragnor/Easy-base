/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.domain.port.out;

import com.easybase.core.user.entity.User;
import com.easybase.core.user.entity.UserCredential;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Akhash R
 */
public interface LoadUserPort {

	public Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

	public Optional<User> findById(UUID userId);

	public Optional<UserCredential> findCredentialByUserId(
		UUID userId, String credentialType);

	public boolean verifyPassword(String plainPassword, String hashedPassword);

}