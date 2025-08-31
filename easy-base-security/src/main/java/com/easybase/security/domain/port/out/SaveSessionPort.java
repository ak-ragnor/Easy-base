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

import com.easybase.security.domain.model.AuthSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaveSessionPort {

	public void deleteExpiredSessions();

	public List<AuthSession> findActiveByUserId(UUID userId);

	public List<AuthSession> findActiveByUserIdAndTenantId(
		UUID userId, UUID tenantId);

	public Optional<AuthSession> findById(UUID sessionId);

	public Optional<AuthSession> findBySessionToken(String sessionToken);

	public void revokeAllByUserId(UUID userId);

	public void revokeAllByUserIdAndTenantId(UUID userId, UUID tenantId);

	public AuthSession save(AuthSession session);

}