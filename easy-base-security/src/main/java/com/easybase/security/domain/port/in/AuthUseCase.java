package com.easybase.security.domain.port.in;

import java.util.UUID;

import com.easybase.security.dto.LoginRequest;
import com.easybase.security.dto.RefreshTokenRequest;
import com.easybase.security.dto.TokenResponse;

public interface AuthUseCase {

	TokenResponse login(LoginRequest request, String userAgent,
			String ipAddress);

	TokenResponse refresh(RefreshTokenRequest request, String userAgent,
			String ipAddress);

	void revoke(String sessionToken);

	void revokeAll(UUID userId, UUID tenantId);

	boolean validateToken(String sessionToken);

	UUID getCurrentUserId(String sessionToken);

	UUID getCurrentTenantId(String sessionToken);
}
