package com.easybase.security.domain.port.out;

import java.time.Instant;

public interface TokenBlacklistPort {

	void blacklistToken(String token, Instant expiration);

	boolean isTokenBlacklisted(String token);

	void cleanupExpiredTokens();
}
