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

import com.easybase.security.domain.model.RefreshToken;
import com.easybase.security.domain.model.mapper.RefreshTokenMapper;
import com.easybase.security.domain.port.out.RefreshTokenPort;
import com.easybase.security.persistence.entity.RefreshTokenEntity;
import com.easybase.security.persistence.repository.RefreshTokenRepository;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefreshTokenAdapter implements RefreshTokenPort {

	@Override
	@Transactional
	public void deleteExpiredTokens() {
		_refreshTokenRepository.deleteExpiredTokens(Instant.now());
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RefreshToken> findActiveBySessionId(UUID sessionId) {
		Optional<RefreshTokenEntity> entityOptional =
			_refreshTokenRepository.findActiveBySessionId(sessionId);

		return entityOptional.map(_refreshTokenMapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RefreshToken> findActiveByUserIdAndTenantId(
		UUID userId, UUID tenantId) {

		List<RefreshTokenEntity> entities =
			_refreshTokenRepository.findActiveByUserIdAndTenantId(
				userId, tenantId);

		Stream<RefreshTokenEntity> entityStream = entities.stream();

		Stream<RefreshToken> mappedStream = entityStream.map(
			_refreshTokenMapper::toDomain);

		return mappedStream.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RefreshToken> findById(UUID tokenId) {
		Optional<RefreshTokenEntity> entityOptional =
			_refreshTokenRepository.findByIdAndRevokedFalse(tokenId);

		return entityOptional.map(_refreshTokenMapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RefreshToken> findByRotationParentId(String parentId) {
		List<RefreshTokenEntity> entities =
			_refreshTokenRepository.findByRotationParentId(parentId);

		Stream<RefreshTokenEntity> entityStream = entities.stream();

		Stream<RefreshToken> mappedStream = entityStream.map(
			_refreshTokenMapper::toDomain);

		return mappedStream.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void revokeAllByUserIdAndTenantId(UUID userId, UUID tenantId) {
		_refreshTokenRepository.revokeAllByUserIdAndTenantId(userId, tenantId);
	}

	@Override
	@Transactional
	public void revokeBySessionId(UUID sessionId) {
		_refreshTokenRepository.revokeBySessionId(sessionId);
	}

	@Override
	@Transactional
	public RefreshToken save(RefreshToken refreshToken) {
		RefreshTokenEntity entity = _refreshTokenMapper.toEntity(refreshToken);

		RefreshTokenEntity saved = _refreshTokenRepository.save(entity);

		return _refreshTokenMapper.toDomain(saved);
	}

	private final RefreshTokenMapper _refreshTokenMapper;
	private final RefreshTokenRepository _refreshTokenRepository;

}