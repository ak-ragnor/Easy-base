/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
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