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

package com.easybase.security.adapter.out.security;

import com.easybase.security.domain.model.AuthSession;
import com.easybase.security.domain.model.mapper.AuthSessionMapper;
import com.easybase.security.domain.port.out.SaveSessionPort;
import com.easybase.security.persistence.entity.AuthSessionEntity;
import com.easybase.security.persistence.repository.AuthSessionRepository;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionJpaAdapter implements SaveSessionPort {

	@Override
	public void deleteExpiredSessions() {
		_authSessionRepository.deleteExpiredSessions(Instant.now());
	}

	@Override
	public List<AuthSession> findActiveByUserId(UUID userId) {
		List<AuthSessionEntity> authSessionEntities =
			_authSessionRepository.findActiveByUserId(userId, Instant.now());

		Stream<AuthSessionEntity> entityStream = authSessionEntities.stream();

		Stream<AuthSession> mappedStream = entityStream.map(
			_authSessionMapper::toDomain);

		return mappedStream.collect(Collectors.toList());
	}

	@Override
	public List<AuthSession> findActiveByUserIdAndTenantId(
		UUID userId, UUID tenantId) {

		List<AuthSessionEntity> authSessionEntities =
			_authSessionRepository.findActiveByUserIdAndTenantId(
				userId, tenantId, Instant.now());

		Stream<AuthSessionEntity> entityStream = authSessionEntities.stream();

		Stream<AuthSession> mappedStream = entityStream.map(
			_authSessionMapper::toDomain);

		return mappedStream.collect(Collectors.toList());
	}

	@Override
	public Optional<AuthSession> findById(UUID sessionId) {
		return _authSessionRepository.findById(
			sessionId
		).map(
			_authSessionMapper::toDomain
		);
	}

	@Override
	public Optional<AuthSession> findBySessionToken(String sessionToken) {
		return _authSessionRepository.findBySessionToken(
			sessionToken
		).map(
			_authSessionMapper::toDomain
		);
	}

	@Override
	public void revokeAllByUserId(UUID userId) {
		_authSessionRepository.revokeAllByUserId(userId);
	}

	@Override
	public void revokeAllByUserIdAndTenantId(UUID userId, UUID tenantId) {
		_authSessionRepository.revokeAllByUserIdAndTenantId(userId, tenantId);
	}

	@Override
	public AuthSession save(AuthSession session) {
		AuthSessionEntity entity = _authSessionRepository.save(
			_authSessionMapper.toEntity(session));

		return _authSessionMapper.toDomain(entity);
	}

	private final AuthSessionMapper _authSessionMapper;
	private final AuthSessionRepository _authSessionRepository;

}