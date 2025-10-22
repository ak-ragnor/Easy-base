/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.session.task;

import com.easybase.security.api.service.SessionService;
import com.easybase.security.session.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task responsible for cleaning up expired sessions and refresh tokens.
 *
 * <p>This task runs periodically to maintain database hygiene by removing
 * expired session data and old revoked tokens.</p>
 *
 * @author Akhash
 */
@Component
@RequiredArgsConstructor
public class SessionCleanupTask {

	@Scheduled(
		fixedRateString = "${easy.base.security.session.cleanup-interval:PT1H}"
	)
	public void cleanupExpiredSessions() {
		_sessionService.cleanupExpiredSessions();
		_refreshTokenService.cleanupExpiredTokens();
	}

	private final RefreshTokenService _refreshTokenService;
	private final SessionService _sessionService;

}