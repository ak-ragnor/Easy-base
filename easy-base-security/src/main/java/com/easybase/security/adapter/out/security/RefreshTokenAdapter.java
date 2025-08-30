package com.easybase.security.adapter.out.security;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.easybase.security.domain.model.RefreshToken;
import com.easybase.security.domain.model.mapper.RefreshTokenMapper;
import com.easybase.security.domain.port.out.RefreshTokenPort;
import com.easybase.security.persistence.entity.RefreshTokenEntity;
import com.easybase.security.persistence.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenAdapter implements RefreshTokenPort {

	@Override
	@Transactional
	public RefreshToken save(RefreshToken refreshToken) {
		RefreshTokenEntity saved = _refreshTokenRepository
				.save(_refreshTokenMapper.toEntity(refreshToken));

		return _refreshTokenMapper.toDomain(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RefreshToken> findById(UUID tokenId) {
		return _refreshTokenRepository.findByIdAndRevokedFalse(tokenId)
				.map(_refreshTokenMapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RefreshToken> findActiveBySessionId(UUID sessionId) {
		return _refreshTokenRepository.findActiveBySessionId(sessionId)
				.map(_refreshTokenMapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RefreshToken> findActiveByUserIdAndTenantId(UUID userId,
			UUID tenantId) {

		return _refreshTokenRepository
				.findActiveByUserIdAndTenantId(userId, tenantId).stream()
				.map(_refreshTokenMapper::toDomain)
				.collect(Collectors.toList());
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
	public void deleteExpiredTokens() {

		_refreshTokenRepository.deleteExpiredTokens(Instant.now());
	}

	@Override
	@Transactional(readOnly = true)
	public List<RefreshToken> findByRotationParentId(String parentId) {
		return _refreshTokenRepository.findByRotationParentId(parentId).stream()
				.map(_refreshTokenMapper::toDomain)
				.collect(Collectors.toList());
	}

	private final RefreshTokenRepository _refreshTokenRepository;

	private final RefreshTokenMapper _refreshTokenMapper;

}
