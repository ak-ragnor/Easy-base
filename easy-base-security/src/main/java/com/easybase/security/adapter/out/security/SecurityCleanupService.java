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

package com.easybase.security.adapter.out.security;

import com.easybase.security.domain.port.out.RefreshTokenPort;
import com.easybase.security.domain.port.out.SaveSessionPort;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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