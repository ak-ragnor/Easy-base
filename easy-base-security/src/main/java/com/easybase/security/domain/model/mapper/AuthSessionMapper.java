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

import com.easybase.security.domain.model.AuthSession;
import com.easybase.security.persistence.entity.AuthSessionEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

@Component
public class AuthSessionMapper {

	public AuthSession toDomain(AuthSessionEntity entity) {
		if (entity == null) {
			return null;
		}

		Instant createdAt = Instant.now();

		if (entity.getCreatedAt() != null) {
			LocalDateTime createdAtLocal = entity.getCreatedAt();

			ZonedDateTime createdAtZoned = createdAtLocal.atZone(
				ZoneOffset.UTC);

			createdAt = createdAtZoned.toInstant();
		}

		Instant updatedAt = Instant.now();

		if (entity.getUpdatedAt() != null) {
			LocalDateTime updatedAtLocal = entity.getUpdatedAt();

			ZonedDateTime updatedAtZoned = updatedAtLocal.atZone(
				ZoneOffset.UTC);

			updatedAt = updatedAtZoned.toInstant();
		}

		return AuthSession.builder(
		).id(
			entity.getId()
		).userId(
			entity.getUserId()
		).tenantId(
			entity.getTenantId()
		).sessionToken(
			entity.getSessionToken()
		).expiresAt(
			entity.getExpiresAt()
		).userAgent(
			entity.getUserAgent()
		).ipAddress(
			entity.getIpAddress()
		).revoked(
			entity.isRevoked()
		).createdAt(
			createdAt
		).updatedAt(
			updatedAt
		).build();
	}

	public AuthSessionEntity toEntity(AuthSession domain) {
		if (domain == null) {
			return null;
		}

		AuthSessionEntity entity = AuthSessionEntity.builder(
		).userId(
			domain.getUserId()
		).tenantId(
			domain.getTenantId()
		).sessionToken(
			domain.getSessionToken()
		).expiresAt(
			domain.getExpiresAt()
		).userAgent(
			domain.getUserAgent()
		).ipAddress(
			domain.getIpAddress()
		).revoked(
			domain.isRevoked()
		).build();

		if (domain.getId() != null) {
			entity.setId(domain.getId());
		}

		return entity;
	}

}