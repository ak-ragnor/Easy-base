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

import com.easybase.security.domain.port.out.TokenBlacklistPort;

import java.time.Instant;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenBlacklistAdapter implements TokenBlacklistPort {

	@Override
	public void blacklistToken(String token, Instant expiration) {
		_blacklistedTokenMap.put(token, expiration);

		log.debug(
			"Token blacklisted: {} expires at {}",
			token.substring(0, Math.min(token.length(), 10)) + "...",
			expiration);
	}

	@Override
	@Scheduled(fixedRate = 3600000)
	public void cleanupExpiredTokens() {
		int initialSize = _blacklistedTokenMap.size();

		Set<Map.Entry<String, Instant>> entries =
			_blacklistedTokenMap.entrySet();

		entries.removeIf(
			entry -> {
				Instant expiryTime = entry.getValue();
				Instant now = Instant.now();

				return expiryTime.isBefore(now);
			});

		int finalSize = _blacklistedTokenMap.size();

		if (initialSize > finalSize) {
			log.debug(
				"Cleaned up {} expired blacklisted tokens",
				initialSize - finalSize);
		}
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

	private final ConcurrentMap<String, Instant> _blacklistedTokenMap =
		new ConcurrentHashMap<>();

}