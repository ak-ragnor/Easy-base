package com.easybase.security.domain.port.in;

import java.util.UUID;

import com.easybase.security.dto.TokenResponse;

public interface AuthUseCase {

	TokenResponse login(UUID tenantId, String email, String password,
			String userAgent, String ipAddress);

	TokenResponse refresh(String refreshToken, String userAgent,
			String ipAddress);

	void revoke(String sessionToken);

	void revokeAll(UUID userId, UUID tenantId);

	boolean validateToken(String sessionToken);

	UUID getCurrentUserId(String sessionToken);

	UUID getCurrentTenantId(String sessionToken);
}
