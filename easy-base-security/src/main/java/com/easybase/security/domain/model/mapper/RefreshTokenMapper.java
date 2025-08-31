/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.security.domain.model.mapper;

import com.easybase.security.domain.model.RefreshToken;
import com.easybase.security.persistence.entity.RefreshTokenEntity;

import org.springframework.stereotype.Component;

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