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

import com.easybase.security.persistence.entity.AuthSessionEntity;

@Repository
public interface AuthSessionRepository
		extends JpaRepository<AuthSessionEntity, UUID> {

	Optional<AuthSessionEntity> findBySessionToken(String sessionToken);

	@Query("SELECT s FROM AuthSessionEntity s WHERE s.userId = :userId AND s.revoked = false AND s.expiresAt > :now")
	List<AuthSessionEntity> findActiveByUserId(@Param("userId") UUID userId,
			@Param("now") Instant now);

	@Query("SELECT s FROM AuthSessionEntity s WHERE s.userId = :userId AND s.tenantId = :tenantId AND s.revoked = false AND s.expiresAt > :now")
	List<AuthSessionEntity> findActiveByUserIdAndTenantId(
			@Param("userId") UUID userId, @Param("tenantId") UUID tenantId,
			@Param("now") Instant now);

	@Modifying
	@Query("UPDATE AuthSessionEntity s SET s.revoked = true WHERE s.userId = :userId")
	void revokeAllByUserId(@Param("userId") UUID userId);

	@Modifying
	@Query("UPDATE AuthSessionEntity s SET s.revoked = true WHERE s.userId = :userId AND s.tenantId = :tenantId")
	void revokeAllByUserIdAndTenantId(@Param("userId") UUID userId,
			@Param("tenantId") UUID tenantId);

	@Modifying
	@Query("DELETE FROM AuthSessionEntity s WHERE s.expiresAt < :cutoff")
	void deleteExpiredSessions(@Param("cutoff") Instant cutoff);
}
