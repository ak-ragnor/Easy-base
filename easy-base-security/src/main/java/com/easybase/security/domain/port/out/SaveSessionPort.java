/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
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