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

package com.easybase.security.domain.port.out;

import com.easybase.core.user.entity.User;
import com.easybase.core.user.entity.UserCredential;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {

	public Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

	public Optional<User> findById(UUID userId);

	public Optional<UserCredential> findCredentialByUserId(
		UUID userId, String credentialType);

	public boolean verifyPassword(String plainPassword, String hashedPassword);

}