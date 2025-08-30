package com.easybase.security.domain.model.mapper;

import org.springframework.stereotype.Component;

import com.easybase.security.domain.model.RefreshToken;
import com.easybase.security.persistence.entity.RefreshTokenEntity;

@Component
public class RefreshTokenMapper {

	public RefreshToken toDomain(RefreshTokenEntity entity) {
		if (entity == null) {
			return null;
		}

		return RefreshToken.builder().id(entity.getId())
				.userId(entity.getUserId()).tenantId(entity.getTenantId())
				.sessionId(entity.getSessionId()).issuedAt(entity.getIssuedAt())
				.expiresAt(entity.getExpiresAt()).revoked(entity.isRevoked())
				.rotationParentId(entity.getRotationParentId()).build();
	}

	public RefreshTokenEntity toEntity(RefreshToken domain) {
		if (domain == null) {
			return null;
		}

		RefreshTokenEntity entity = RefreshTokenEntity.builder()
				.userId(domain.getUserId()).tenantId(domain.getTenantId())
				.sessionId(domain.getSessionId()).issuedAt(domain.getIssuedAt())
				.expiresAt(domain.getExpiresAt()).revoked(domain.isRevoked())
				.rotationParentId(domain.getRotationParentId()).build();

		if (domain.getId() != null) {
			entity.setId(domain.getId());
		}

		return entity;
	}
}
