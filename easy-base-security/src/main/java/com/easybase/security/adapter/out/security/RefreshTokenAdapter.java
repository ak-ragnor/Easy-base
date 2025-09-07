/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.adapter.out.security;

import com.easybase.security.adapter.out.persistence.entity.RefreshTokenEntity;
import com.easybase.security.adapter.out.persistence.repository.RefreshTokenRepository;
import com.easybase.security.domain.model.RefreshToken;
import com.easybase.security.domain.model.mapper.RefreshTokenMapper;
import com.easybase.security.domain.port.out.RefreshTokenPort;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Akhash R
 */
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