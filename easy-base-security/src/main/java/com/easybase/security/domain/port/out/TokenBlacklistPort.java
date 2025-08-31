/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.domain.port.out;

import java.time.Instant;

public interface TokenBlacklistPort {

	public void blacklistToken(String token, Instant expiration);

	public void cleanupExpiredTokens();

	public boolean isTokenBlacklisted(String token);

}