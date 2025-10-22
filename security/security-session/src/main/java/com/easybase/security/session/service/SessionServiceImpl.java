/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.session.service;

import com.easybase.security.api.dto.CreateSessionRequest;
import com.easybase.security.api.dto.Session;
import com.easybase.security.api.service.SessionService;
import com.easybase.security.session.config.SessionProperties;
import com.easybase.security.session.entity.SessionEntity;
import com.easybase.security.session.repository.SessionRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link SessionService} that manages user sessions including
 * creation, retrieval, revocation, and cleanup operations.
 *
 * <p>This service handles session lifecycle management with support for
 * session limits, sliding expiration, and automatic cleanup of expired sessions.</p>
 *
 * @author Akhash
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SessionServiceImpl implements SessionService {

	@Override
	@Transactional
	public void cleanupExpiredSessions() {
		Instant now = Instant.now();

		Instant revokedBefore = now.minus(
			_sessionProperties.getCleanupGracePeriod());

		_sessionRepository.deleteExpiredAndOldRevokedSessions(
			now, revokedBefore);
	}

	@Override
	@Transactional
	public Session createSession(CreateSessionRequest request) {
		Instant now = Instant.now();

		Duration ttl = (request.getTtl() != null) ? request.getTtl() :
			_sessionProperties.getDefaultTtl();

		Instant expiresAt = now.plus(ttl);

		_enforceSessionLimit(request.getUserId(), request.getTenantId());

		String sessionId = String.valueOf(UUID.randomUUID());

		String metadataJSON = null;

		Map<String, Object> metadataMap = request.getMetadata();

		if ((metadataMap != null) && !metadataMap.isEmpty()) {
			try {
				metadataJSON = _objectMapper.writeValueAsString(metadataMap);
			}
			catch (JsonProcessingException jsonProcessingException) {
				log.warn(
					"Failed to serialize session metadata",
					jsonProcessingException);
			}
		}

		SessionEntity sessionEntity = new SessionEntity();

		sessionEntity.setSessionId(sessionId);
		sessionEntity.setUserId(request.getUserId());
		sessionEntity.setTenantId(request.getTenantId());
		sessionEntity.setExpiresAt(expiresAt);
		sessionEntity.setClientIp(request.getClientIp());
		sessionEntity.setUserAgent(request.getUserAgent());
		sessionEntity.setDeviceInfo(request.getDeviceInfo());
		sessionEntity.setMetadata(metadataJSON);

		SessionEntity saved = _sessionRepository.save(sessionEntity);

		return _mapToDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Session> getSession(String sessionId) {
		return _sessionRepository.findBySessionIdAndRevokedFalse(
			sessionId
		).filter(
			this::_isNotExpired
		).map(
			this::_mapToDto
		);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Session> listSessions(UUID userId, UUID tenantId) {
		List<SessionEntity> sessionEntitiesList =
			_sessionRepository.
				findByUserIdAndTenantIdAndRevokedFalseOrderByCreatedAtDesc(
					userId, tenantId);

		Stream<SessionEntity> sessionEntitiesStream =
			sessionEntitiesList.stream();

		return sessionEntitiesStream.filter(
			this::_isNotExpired
		).map(
			this::_mapToDto
		).toList();
	}

	@Override
	@Transactional
	public void revokeSession(String sessionId) {
		_sessionRepository.revokeBySessionId(sessionId, Instant.now());
	}

	@Override
	@Transactional
	public void revokeSessionsForUser(UUID userId, UUID tenantId) {
		_sessionRepository.revokeAllByUserIdAndTenantId(
			userId, tenantId, Instant.now());
	}

	@Override
	@Transactional
	public void touchSession(String sessionId) {
		Instant now = Instant.now();

		_sessionRepository.updateLastAccessedTime(sessionId, now);

		if (_sessionProperties.isSlidingExpiration()) {
			Instant newExpiresAt = now.plus(_sessionProperties.getDefaultTtl());

			_sessionRepository.updateSessionExpiration(sessionId, newExpiresAt);
		}
	}

	private void _enforceSessionLimit(UUID userId, UUID tenantId) {
		int maxSessions = _sessionProperties.getMaxSessionsPerUser();

		if (maxSessions <= 0) {
			return;
		}

		List<SessionEntity> activeSessions =
			_sessionRepository.
				findByUserIdAndTenantIdAndRevokedFalseOrderByCreatedAtDesc(
					userId, tenantId);

		Stream<SessionEntity> activeSessionsStream = activeSessions.stream();

		List<SessionEntity> nonexpiredSessions = activeSessionsStream.filter(
			this::_isNotExpired
		).toList();

		if (nonexpiredSessions.size() >= maxSessions) {
			int sessionsToRevoke = nonexpiredSessions.size() - maxSessions + 1;

			Stream<SessionEntity> nonexpiredSessionsStream =
				nonexpiredSessions.stream();

			List<SessionEntity> oldestSessions = nonexpiredSessionsStream.skip(
				nonexpiredSessions.size() - sessionsToRevoke
			).toList();

			for (SessionEntity sessionEntity : oldestSessions) {
				_sessionRepository.revokeBySessionId(
					sessionEntity.getSessionId(), Instant.now());
			}
		}
	}

	private boolean _isNotExpired(SessionEntity entity) {
		Instant expiresAt = entity.getExpiresAt();
		Instant now = Instant.now();

		return expiresAt.isAfter(now);
	}

	private Session _mapToDto(SessionEntity entity) {
		Map<String, Object> metadata = null;

		if (entity.getMetadata() != null) {
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> parsed = _objectMapper.readValue(
					entity.getMetadata(), Map.class);

				metadata = parsed;
			}
			catch (JsonProcessingException jsonProcessingException) {
				log.warn(
					"Failed to deserialize session metadata for session {}",
					entity.getSessionId(), jsonProcessingException);
			}
		}

		Session session = new Session();

		session.setSessionId(entity.getSessionId());
		session.setUserId(entity.getUserId());
		session.setTenantId(entity.getTenantId());
		session.setCreatedAt(entity.getCreatedAt());
		session.setLastAccessedAt(entity.getLastAccessedAt());
		session.setExpiresAt(entity.getExpiresAt());
		session.setRevoked(entity.getRevoked());
		session.setRevokedAt(entity.getRevokedAt());
		session.setClientIp(entity.getClientIp());
		session.setUserAgent(entity.getUserAgent());
		session.setDeviceInfo(entity.getDeviceInfo());
		session.setMetadata(metadata);

		return session;
	}

	private final ObjectMapper _objectMapper;
	private final SessionProperties _sessionProperties;
	private final SessionRepository _sessionRepository;

}