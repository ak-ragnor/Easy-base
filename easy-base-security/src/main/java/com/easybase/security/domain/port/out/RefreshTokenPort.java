package com.easybase.security.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.easybase.security.domain.model.RefreshToken;

public interface RefreshTokenPort {

	RefreshToken save(RefreshToken refreshToken);

	Optional<RefreshToken> findById(UUID tokenId);

	Optional<RefreshToken> findActiveBySessionId(UUID sessionId);

	List<RefreshToken> findActiveByUserIdAndTenantId(UUID userId,
			UUID tenantId);

	void revokeAllByUserIdAndTenantId(UUID userId, UUID tenantId);

	void revokeBySessionId(UUID sessionId);

	void deleteExpiredTokens();

	List<RefreshToken> findByRotationParentId(String parentId);
}
