package com.easybase.security.adapter.out.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybase.security.domain.port.out.RefreshTokenPort;
import com.easybase.security.domain.port.out.SaveSessionPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityCleanupService {

	@Scheduled(fixedRate = 21600000)
	@Transactional
	public void cleanupExpiredSessions() {
		try {
			_saveSessionPort.deleteExpiredSessions();

			log.debug("Cleaned up expired auth sessions");
		} catch (Exception e) {
			log.error("Failed to cleanup expired sessions", e);
		}
	}

	@Scheduled(fixedRate = 21600000)
	@Transactional
	public void cleanupExpiredRefreshTokens() {
		try {
			_refreshTokenPort.deleteExpiredTokens();

			log.debug("Cleaned up expired refresh tokens");
		} catch (Exception e) {
			log.error("Failed to cleanup expired refresh tokens", e);
		}
	}

	private final SaveSessionPort _saveSessionPort;

	private final RefreshTokenPort _refreshTokenPort;
}
