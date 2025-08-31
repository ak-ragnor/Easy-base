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

import com.easybase.security.domain.model.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenPort {

	public void deleteExpiredTokens();

	public Optional<RefreshToken> findActiveBySessionId(UUID sessionId);

	public List<RefreshToken> findActiveByUserIdAndTenantId(
		UUID userId, UUID tenantId);

	public Optional<RefreshToken> findById(UUID tokenId);

	public List<RefreshToken> findByRotationParentId(String parentId);

	public void revokeAllByUserIdAndTenantId(UUID userId, UUID tenantId);

	public void revokeBySessionId(UUID sessionId);

	public RefreshToken save(RefreshToken refreshToken);

}