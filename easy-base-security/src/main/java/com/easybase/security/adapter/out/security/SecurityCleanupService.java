/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.adapter.out.security;

import com.easybase.security.domain.port.out.RefreshTokenPort;
import com.easybase.security.domain.port.out.SaveSessionPort;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SecurityCleanupService {

	@Scheduled(fixedRate = 21600000)
	@Transactional
	public void cleanupExpiredRefreshTokens() {
		try {
			_refreshTokenPort.deleteExpiredTokens();

			log.debug("Cleaned up expired refresh tokens");
		}
		catch (Exception exception) {
			log.error("Failed to cleanup expired refresh tokens", exception);
		}
	}

	@Scheduled(fixedRate = 21600000)
	@Transactional
	public void cleanupExpiredSessions() {
		try {
			_saveSessionPort.deleteExpiredSessions();

			log.debug("Cleaned up expired auth sessions");
		}
		catch (Exception exception) {
			log.error("Failed to cleanup expired sessions", exception);
		}
	}

	private final RefreshTokenPort _refreshTokenPort;
	private final SaveSessionPort _saveSessionPort;

}