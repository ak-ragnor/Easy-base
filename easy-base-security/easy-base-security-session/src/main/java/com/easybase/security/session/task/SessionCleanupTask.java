/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.session.task;

import com.easybase.security.api.service.SessionService;
import com.easybase.security.session.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Akhash
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionCleanupTask {

	@Scheduled(
		fixedRateString = "${easy.base.security.session.cleanup-interval:PT1H}"
	)
	public void cleanupExpiredSessions() {
		try {
			log.debug("Starting session cleanup task");

			sessionService.cleanupExpiredSessions();
			refreshTokenService.cleanupExpiredTokens();

			log.debug("Session cleanup task completed");
		}
		catch (Exception exception) {
			log.error("Error during session cleanup", exception);
		}
	}

	private final RefreshTokenService refreshTokenService;
	private final SessionService sessionService;

}