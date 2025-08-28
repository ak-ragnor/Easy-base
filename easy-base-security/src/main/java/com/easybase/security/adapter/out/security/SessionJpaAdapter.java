package com.easybase.security.adapter.out.security;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.easybase.security.domain.model.AuthSession;
import com.easybase.security.domain.model.mapper.AuthSessionMapper;
import com.easybase.security.domain.port.out.SaveSessionPort;
import com.easybase.security.persistence.entity.AuthSessionEntity;
import com.easybase.security.persistence.repository.AuthSessionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionJpaAdapter implements SaveSessionPort {

	@Override
	public AuthSession save(AuthSession session) {
		AuthSessionEntity entity = _authSessionRepository
				.save(_authSessionMapper.toEntity(session));

		return _authSessionMapper.toDomain(entity);
	}

	@Override
	public Optional<AuthSession> findById(UUID sessionId) {

		return _authSessionRepository.findById(sessionId)
				.map(_authSessionMapper::toDomain);
	}

	@Override
	public Optional<AuthSession> findBySessionToken(String sessionToken) {

		return _authSessionRepository.findBySessionToken(sessionToken)
				.map(_authSessionMapper::toDomain);
	}

	@Override
	public List<AuthSession> findActiveByUserId(UUID userId) {

		return _authSessionRepository.findActiveByUserId(userId, Instant.now())
				.stream().map(_authSessionMapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public List<AuthSession> findActiveByUserIdAndTenantId(UUID userId,
			UUID tenantId) {

		return _authSessionRepository
				.findActiveByUserIdAndTenantId(userId, tenantId, Instant.now())
				.stream().map(_authSessionMapper::toDomain)
				.collect(Collectors.toList());
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
	public void deleteExpiredSessions() {

		_authSessionRepository.deleteExpiredSessions(Instant.now());
	}

	private final AuthSessionRepository _authSessionRepository;

	private final AuthSessionMapper _authSessionMapper;

}
