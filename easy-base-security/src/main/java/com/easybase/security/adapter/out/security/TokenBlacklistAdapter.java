package com.easybase.security.adapter.out.security;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.easybase.security.domain.port.out.TokenBlacklistPort;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenBlacklistAdapter implements TokenBlacklistPort {

	@Override
	public void blacklistToken(String token, Instant expiration) {
		_blacklistedTokenMap.put(token, expiration);

		log.debug("Token blacklisted: {} expires at {}",
				token.substring(0, Math.min(token.length(), 10)) + "...",
				expiration);
	}

	@Override
	public boolean isTokenBlacklisted(String token) {
		Instant expiration = _blacklistedTokenMap.get(token);

		if (expiration == null) {
			return false;
		}

		if (expiration.isBefore(Instant.now())) {
			_blacklistedTokenMap.remove(token);
			return false;
		}

		return true;
	}

	@Override
	@Scheduled(fixedRate = 3600000)
	public void cleanupExpiredTokens() {
		int initialSize = _blacklistedTokenMap.size();

		_blacklistedTokenMap.entrySet()
				.removeIf(entry -> entry.getValue().isBefore(Instant.now()));

		int finalSize = _blacklistedTokenMap.size();

		if (initialSize > finalSize) {
			log.debug("Cleaned up {} expired blacklisted tokens",
					initialSize - finalSize);
		}
	}

	private final ConcurrentMap<String, Instant> _blacklistedTokenMap = new ConcurrentHashMap<>();
}
