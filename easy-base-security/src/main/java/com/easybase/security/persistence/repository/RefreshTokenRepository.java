package com.easybase.security.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.easybase.security.persistence.entity.RefreshTokenEntity;

@Repository
public interface RefreshTokenRepository
		extends JpaRepository<RefreshTokenEntity, UUID> {

	Optional<RefreshTokenEntity> findByIdAndRevokedFalse(UUID tokenId);

	@Query("SELECT rt FROM RefreshTokenEntity rt WHERE rt.sessionId = :sessionId AND rt.revoked = false")
	Optional<RefreshTokenEntity> findActiveBySessionId(
			@Param("sessionId") UUID sessionId);

	@Query("SELECT rt FROM RefreshTokenEntity rt WHERE rt.userId = :userId AND rt.tenantId = :tenantId AND rt.revoked = false")
	List<RefreshTokenEntity> findActiveByUserIdAndTenantId(
			@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

	@Modifying
	@Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true WHERE rt.userId = :userId AND rt.tenantId = :tenantId")
	void revokeAllByUserIdAndTenantId(@Param("userId") UUID userId,
			@Param("tenantId") UUID tenantId);

	@Modifying
	@Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true WHERE rt.sessionId = :sessionId")
	void revokeBySessionId(@Param("sessionId") UUID sessionId);

	@Modifying
	@Query("DELETE FROM RefreshTokenEntity rt WHERE rt.expiresAt < :now")
	void deleteExpiredTokens(@Param("now") Instant now);

	@Query("SELECT rt FROM RefreshTokenEntity rt WHERE rt.rotationParentId = :parentId AND rt.revoked = false")
	List<RefreshTokenEntity> findByRotationParentId(
			@Param("parentId") String parentId);
}
