/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.api.service;

import com.easybase.security.api.dto.CreateSessionRequest;
import com.easybase.security.api.dto.Session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Akhash
 */
public interface SessionService {

	public void cleanupExpiredSessions();

	public Session createSession(CreateSessionRequest request);

	public Optional<Session> getSession(String sessionId);

	public List<Session> listSessions(UUID userId, UUID tenantId);

	public void revokeSession(String sessionId);

	public void revokeSessionsForUser(UUID userId, UUID tenantId);

	public void touchSession(String sessionId);

}