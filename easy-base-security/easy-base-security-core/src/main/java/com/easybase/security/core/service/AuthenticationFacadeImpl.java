/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.core.service;

import com.easybase.core.user.entity.User;
import com.easybase.core.user.service.UserService;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.api.dto.CreateSessionRequest;
import com.easybase.security.api.dto.LoginRequest;
import com.easybase.security.api.dto.Session;
import com.easybase.security.api.dto.TokenClaims;
import com.easybase.security.api.dto.TokenValidationResult;
import com.easybase.security.api.exception.AuthenticationException;
import com.easybase.security.api.service.AuthenticationFacade;
import com.easybase.security.api.service.SessionService;
import com.easybase.security.api.service.TokenService;
import com.easybase.security.session.config.SessionProperties;
import com.easybase.security.session.entity.RefreshTokenEntity;
import com.easybase.security.session.service.RefreshTokenService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * @author Akhash
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AuthenticationFacadeImpl implements AuthenticationFacade {

	@Override
	public AuthenticatedPrincipalData authenticateByToken(String token) {
		TokenValidationResult validationResult =
			_tokenService.validateAccessToken(token);

		if (!validationResult.isValid()) {
			Optional<String> optionalReason = validationResult.getReason();

			String reason = optionalReason.orElse("Unknown");

			String errorMessage = "Invalid token: " + reason;

			throw new AuthenticationException(errorMessage);
		}

		Optional<TokenClaims> optionalClaims = validationResult.getClaims();

		TokenClaims claims = optionalClaims.orElseThrow();

		Optional<Session> session = _sessionService.getSession(
			claims.getSessionId());

		if (session.isEmpty()) {
			throw new AuthenticationException("Session not found or expired");
		}

		Session activeSession = session.get();

		if (activeSession.isRevoked()) {
			throw new AuthenticationException("Session revoked");
		}

		UUID tenantId = claims.getTenantId();

		if (!tenantId.equals(activeSession.getTenantId())) {
			throw new AuthenticationException("Tenant mismatch");
		}

		AuthenticatedPrincipalData principalData =
			new AuthenticatedPrincipalData();

		principalData.setUserId(claims.getUserId());
		principalData.setTenantId(tenantId);
		principalData.setSessionId(claims.getSessionId());
		principalData.setAuthorities(
			(claims.getRoles() != null) ? claims.getRoles() :
				Collections.emptyList());
		principalData.setMetadata(activeSession.getMetadata());
		principalData.setIssuedAt(claims.getIssuedAt());
		principalData.setExpiresAt(claims.getExpiresAt());
		principalData.setClientIp(activeSession.getClientIp());
		principalData.setUserAgent(activeSession.getUserAgent());

		return principalData;
	}

	@Override
	public AuthenticatedPrincipalData authenticateCredentials(
		LoginRequest loginRequest) {

		log.info(
			"Authenticating user: {} for tenant: {}",
			loginRequest.getUserName(), loginRequest.getTenantId());

		// 1. Validate credentials against user service

		User user = _userService.authenticateUser(
			loginRequest.getUserName(), loginRequest.getPassword(),
			loginRequest.getTenantId());

		// 2. Get user authorities/roles

		List<String> authorities = _userService.getUserAuthorities(
			user.getId());

		// 3. Create session with proper user data

		CreateSessionRequest sessionRequest = new CreateSessionRequest();

		sessionRequest.setUserId(user.getId());
		sessionRequest.setTenantId(loginRequest.getTenantId());
		sessionRequest.setTtl(_sessionProperties.getDefaultTtl());
		sessionRequest.setClientIp(loginRequest.getClientIp());
		sessionRequest.setUserAgent(loginRequest.getUserAgent());
		sessionRequest.setDeviceInfo(loginRequest.getDeviceInfo());
		sessionRequest.setMetadata(loginRequest.getMetadata());

		Session session = _sessionService.createSession(sessionRequest);

		AuthenticatedPrincipalData principalData =
			new AuthenticatedPrincipalData();

		principalData.setUserId(session.getUserId());
		principalData.setTenantId(session.getTenantId());
		principalData.setSessionId(session.getSessionId());
		principalData.setAuthorities(authorities);
		principalData.setMetadata(session.getMetadata());
		principalData.setIssuedAt(session.getCreatedAt());
		principalData.setExpiresAt(session.getExpiresAt());
		principalData.setClientIp(session.getClientIp());
		principalData.setUserAgent(session.getUserAgent());

		return principalData;
	}

	@Override
	public void logout(String sessionId) {
		log.info("Logging out session: {}", sessionId);
		_sessionService.revokeSession(sessionId);
		_refreshTokenService.revokeRefreshTokensForSession(sessionId);
	}

	@Override
	public AuthenticatedPrincipalData refreshAuthentication(
		String refreshToken) {

		// 1. Validate refresh token

		Optional<RefreshTokenEntity> tokenEntityOptional =
			_refreshTokenService.validateRefreshToken(refreshToken);

		if (tokenEntityOptional.isEmpty()) {
			throw new AuthenticationException(
				"Invalid or expired refresh token");
		}

		RefreshTokenEntity tokenEntity = tokenEntityOptional.get();

		String sessionId = tokenEntity.getSessionId();

		// 2. Get associated session

		Optional<Session> sessionOptional = _sessionService.getSession(
			sessionId);

		if (sessionOptional.isEmpty()) {
			throw new AuthenticationException("Session not found or expired");
		}

		Session activeSession = sessionOptional.get();

		if (activeSession.isRevoked()) {
			throw new AuthenticationException("Session revoked");
		}

		// 3. Create authenticated principal data

		AuthenticatedPrincipalData principalData =
			new AuthenticatedPrincipalData();

		principalData.setUserId(activeSession.getUserId());
		principalData.setTenantId(activeSession.getTenantId());
		principalData.setSessionId(activeSession.getSessionId());

		List<String> authorities = _userService.getUserAuthorities(
			activeSession.getUserId());

		principalData.setAuthorities(authorities);

		principalData.setMetadata(activeSession.getMetadata());
		principalData.setIssuedAt(activeSession.getCreatedAt());
		principalData.setExpiresAt(activeSession.getExpiresAt());
		principalData.setClientIp(activeSession.getClientIp());
		principalData.setUserAgent(activeSession.getUserAgent());

		return principalData;
	}

	@Override
	public void revokeRefreshToken(String refreshToken) {
		_refreshTokenService.revokeRefreshToken(refreshToken);
		log.debug("Revoked refresh token");
	}

	private final RefreshTokenService _refreshTokenService;
	private final SessionProperties _sessionProperties;
	private final SessionService _sessionService;
	private final TokenService _tokenService;
	private final UserService _userService;

}