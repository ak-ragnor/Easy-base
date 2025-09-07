/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.domain.model.mapper;

import com.easybase.security.adapter.out.persistence.entity.RefreshTokenEntity;
import com.easybase.security.domain.model.RefreshToken;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class RefreshTokenMapper {

	public RefreshToken toDomain(RefreshTokenEntity entity) {
		if (entity == null) {
			return null;
		}

		return RefreshToken.builder(
		).id(
			entity.getId()
		).userId(
			entity.getUserId()
		).tenantId(
			entity.getTenantId()
		).sessionId(
			entity.getSessionId()
		).issuedAt(
			entity.getIssuedAt()
		).expiresAt(
			entity.getExpiresAt()
		).revoked(
			entity.isRevoked()
		).rotationParentId(
			entity.getRotationParentId()
		).build();
	}

	public RefreshTokenEntity toEntity(RefreshToken domain) {
		if (domain == null) {
			return null;
		}

		RefreshTokenEntity entity = RefreshTokenEntity.builder(
		).userId(
			domain.getUserId()
		).tenantId(
			domain.getTenantId()
		).sessionId(
			domain.getSessionId()
		).issuedAt(
			domain.getIssuedAt()
		).expiresAt(
			domain.getExpiresAt()
		).revoked(
			domain.isRevoked()
		).rotationParentId(
			domain.getRotationParentId()
		).build();

		if (domain.getId() != null) {
			entity.setId(domain.getId());
		}

		return entity;
	}

}