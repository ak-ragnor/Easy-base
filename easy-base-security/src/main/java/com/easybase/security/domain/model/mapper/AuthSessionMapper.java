package com.easybase.security.domain.model.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.easybase.security.domain.model.AuthSession;
import com.easybase.security.persistence.entity.AuthSessionEntity;

@Component
public class AuthSessionMapper {

	public AuthSession toDomain(AuthSessionEntity entity) {
		if (entity == null) {
			return null;
		}

		return AuthSession.builder().id(entity.getId())
				.userId(entity.getUserId()).tenantId(entity.getTenantId())
				.sessionToken(entity.getSessionToken())
				.expiresAt(entity.getExpiresAt())
				.userAgent(entity.getUserAgent())
				.ipAddress(entity.getIpAddress()).revoked(entity.isRevoked())
				.createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt()
						.atZone(java.time.ZoneOffset.UTC).toInstant()
						: Instant.now())
				.updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt()
						.atZone(java.time.ZoneOffset.UTC).toInstant()
						: Instant.now())
				.build();
	}

	public AuthSessionEntity toEntity(AuthSession domain) {
		if (domain == null) {
			return null;
		}

		AuthSessionEntity entity = AuthSessionEntity.builder()
				.userId(domain.getUserId()).tenantId(domain.getTenantId())
				.sessionToken(domain.getSessionToken())
				.expiresAt(domain.getExpiresAt())
				.userAgent(domain.getUserAgent())
				.ipAddress(domain.getIpAddress()).revoked(domain.isRevoked())
				.build();

		if (domain.getId() != null) {
			entity.setId(domain.getId());
		}

		return entity;
	}
}
