package com.easybase.security.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.easybase.security.domain.model.AuthSession;

public interface SaveSessionPort {

	AuthSession save(AuthSession session);

	Optional<AuthSession> findById(UUID sessionId);

	Optional<AuthSession> findBySessionToken(String sessionToken);

	List<AuthSession> findActiveByUserId(UUID userId);

	List<AuthSession> findActiveByUserIdAndTenantId(UUID userId, UUID tenantId);

	void revokeAllByUserId(UUID userId);

	void revokeAllByUserIdAndTenantId(UUID userId, UUID tenantId);

	void deleteExpiredSessions();
}
