/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.domain.model.mapper;

import com.easybase.security.domain.model.AuthSession;
import com.easybase.security.persistence.entity.AuthSessionEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
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