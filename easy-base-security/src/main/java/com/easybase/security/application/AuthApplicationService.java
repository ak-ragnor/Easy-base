/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.application;

import com.easybase.common.exception.UnauthorizedException;
import com.easybase.core.tenant.entity.Tenant;
import com.easybase.core.user.entity.User;
import com.easybase.core.user.entity.UserCredential;
import com.easybase.security.adapter.out.security.JwtTokenService;
import com.easybase.security.domain.model.AuthSession;
import com.easybase.security.domain.model.RefreshToken;
import com.easybase.security.domain.port.in.AuthUseCase;
import com.easybase.security.domain.port.out.LoadUserPort;
import com.easybase.security.domain.port.out.RefreshTokenPort;
import com.easybase.security.domain.port.out.SaveSessionPort;
import com.easybase.security.domain.port.out.TokenBlacklistPort;
import com.easybase.security.domain.service.AuthDomainService;
import com.easybase.security.dto.TokenResponse;

import java.time.Duration;
import java.time.Instant;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AuthApplicationService implements AuthUseCase {

	@Override
	@Transactional(readOnly = true)
	public UUID getCurrentTenantId(String sessionToken)
		throws UnauthorizedException {

		AuthSession session = _getSession(sessionToken);

		return session.getTenantId();
	}

	@Override
	@Transactional(readOnly = true)
	public UUID getCurrentUserId(String sessionToken)
		throws UnauthorizedException {

		AuthSession session = _getSession(sessionToken);

		return session.getUserId();
	}

	@Override
	@Transactional
	public TokenResponse login(
			UUID tenantId, String email, String password, String userAgent,
			String ipAddress)
		throws UnauthorizedException {

		try {
			User user = _getAuthenticatedUser(email, password, tenantId);

			AuthSession session = _createAndSaveSession(
				user, userAgent, ipAddress);

			RefreshToken refreshToken = _createAndSaveRefreshToken(
				user, session);

			return _buildTokenResponse(session, refreshToken, user);
		}
		catch (Exception exception) {
			throw new UnauthorizedException(
				"Invalid credentials provided", exception);
		}
	}

	@Override
	@Transactional
	public TokenResponse refresh(
			String refreshTokenId, String userAgent, String ipAddress)
		throws UnauthorizedException {

		try {
			RefreshToken refreshToken = _getValidRefreshToken(refreshTokenId);

			UUID refreshTokenUserId = refreshToken.getUserId();

			User user = _getValidUser(refreshTokenUserId);

			UUID refreshTokenSessionId = refreshToken.getSessionId();

			AuthSession session = _getValidSession(refreshTokenSessionId);

			_tokenBlacklistPort.blacklistToken(
				session.getSessionToken(), session.getExpiresAt());

			UUID userId = user.getId();

			Tenant tenant = user.getTenant();

			String newSessionToken = _generateSessionToken(
				userId, tenant.getId(), user.getEmail());

			AuthSession refreshedSession = _authDomainService.refreshSession(
				session, newSessionToken);

			AuthSession savedSession = _saveSessionPort.save(refreshedSession);

			RefreshToken revoked = _authDomainService.revokeRefreshToken(
				refreshToken);

			_refreshTokenPort.save(revoked);

			UUID savedSessionId = savedSession.getId();

			String rotationParentIdString = String.valueOf(
				refreshToken.getId());

			RefreshToken newRefreshToken =
				_authDomainService.createRefreshToken(
					userId, tenant.getId(), savedSessionId,
					rotationParentIdString);

			RefreshToken savedRefreshToken = _refreshTokenPort.save(
				newRefreshToken);

			return _buildTokenResponse(savedSession, savedRefreshToken, user);
		}
		catch (Exception exception) {
			throw new UnauthorizedException("Token refresh failed", exception);
		}
	}

	@Override
	@Transactional
	public void revoke(String sessionToken) {
		Optional<AuthSession> optionalSession =
			_saveSessionPort.findBySessionToken(sessionToken);

		AuthSession session = optionalSession.orElseThrow(
			() -> new UnauthorizedException("Invalid session token"));

		AuthSession revoked = _authDomainService.revokeSession(session);

		_saveSessionPort.save(revoked);

		UUID sessionId = session.getId();

		_refreshTokenPort.revokeBySessionId(sessionId);

		_tokenBlacklistPort.blacklistToken(
			sessionToken, session.getExpiresAt());

		log.info("Session revoked: sessionId={}", sessionId);
	}

	@Override
	@Transactional
	public void revokeAll(UUID userId, UUID tenantId) {
		_saveSessionPort.revokeAllByUserIdAndTenantId(userId, tenantId);

		_refreshTokenPort.revokeAllByUserIdAndTenantId(userId, tenantId);

		log.info(
			"All sessions revoked for userId={}, tenantId={}", userId,
			tenantId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean validateToken(String sessionToken) {
		boolean blacklisted = _tokenBlacklistPort.isTokenBlacklisted(
			sessionToken);

		if (blacklisted) {
			return false;
		}

		Optional<AuthSession> optionalSession =
			_saveSessionPort.findBySessionToken(sessionToken);

		return optionalSession.map(
			_authDomainService::isSessionValid
		).orElse(
			false
		);
	}

	private TokenResponse _buildTokenResponse(
		AuthSession session, RefreshToken refreshToken, User user) {

		Instant now = Instant.now();
		Instant sessionExpiry = session.getExpiresAt();

		Duration timeLeft = Duration.between(now, sessionExpiry);

		long expiresInSeconds = timeLeft.getSeconds();

		String refreshIdString = String.valueOf(refreshToken.getId());

		TokenResponse tokenResponse = new TokenResponse();

		tokenResponse.setAccessToken(session.getSessionToken());
		tokenResponse.setRefreshToken(refreshIdString);
		tokenResponse.setTokenType("Bearer");
		tokenResponse.setExpiresIn(expiresInSeconds);
		tokenResponse.setExpiresAt(sessionExpiry);
		tokenResponse.setUserId(session.getUserId());
		tokenResponse.setTenantId(session.getTenantId());
		tokenResponse.setUserEmail(user.getEmail());
		tokenResponse.setUserDisplayName(user.getDisplayName());

		return tokenResponse;
	}

	private RefreshToken _createAndSaveRefreshToken(
		User user, AuthSession session) {

		UUID userId = user.getId();
		Tenant tenant = user.getTenant();
		UUID sessionId = session.getId();

		RefreshToken refreshToken = _authDomainService.createRefreshToken(
			userId, tenant.getId(), sessionId, null);

		return _refreshTokenPort.save(refreshToken);
	}

	private AuthSession _createAndSaveSession(
		User user, String userAgent, String ipAddress) {

		UUID userId = user.getId();
		Tenant tenant = user.getTenant();

		String sessionToken = _generateSessionToken(
			userId, tenant.getId(), user.getEmail());

		AuthSession session = _authDomainService.createSession(
			userId, tenant.getId(), sessionToken, userAgent, ipAddress);

		return _saveSessionPort.save(session);
	}

	private String _generateSessionToken(
		UUID userId, UUID tenantId, String email) {

		return _jwtTokenService.generateAccessToken(userId, tenantId, email);
	}

	private User _getAuthenticatedUser(
			String email, String password, UUID tenantId)
		throws UnauthorizedException {

		String sanitizedEmail = email.trim();

		Optional<User> optionalUser = _loadUserPort.findByEmailAndTenantId(
			sanitizedEmail.toLowerCase(), tenantId);

		User user = optionalUser.orElseThrow(
			() -> new UnauthorizedException("User not found"));

		Boolean deleted = user.getDeleted();

		if ((deleted != null) && deleted) {
			throw new UnauthorizedException("User account is deleted");
		}

		Optional<UserCredential> optionalCredential =
			_loadUserPort.findCredentialByUserId(
				user.getId(), _PASSWORD_CREDENTIAL_TYPE);

		UserCredential credential = optionalCredential.orElseThrow(
			() -> new UnauthorizedException("Password credential not found"));

		String hashedPassword = credential.getPasswordHash();

		boolean passwordMissing = false;

		if (hashedPassword == null) {
			passwordMissing = true;
		}

		boolean passwordMismatch = !_loadUserPort.verifyPassword(
			password, hashedPassword);

		if (passwordMissing || passwordMismatch) {
			throw new UnauthorizedException("Password mismatch");
		}

		return user;
	}

	private AuthSession _getSession(String sessionToken)
		throws UnauthorizedException {

		Optional<AuthSession> optionalSession =
			_saveSessionPort.findBySessionToken(sessionToken);

		return optionalSession.filter(
			_authDomainService::isSessionValid
		).orElseThrow(
			() -> new UnauthorizedException("Invalid or expired session token")
		);
	}

	private RefreshToken _getValidRefreshToken(String tokenString)
		throws UnauthorizedException {

		UUID refreshTokenId;

		try {
			refreshTokenId = UUID.fromString(tokenString.trim());
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new UnauthorizedException(
				"Invalid refresh token format", illegalArgumentException);
		}

		Optional<RefreshToken> optionalToken = _refreshTokenPort.findById(
			refreshTokenId);

		RefreshToken refreshToken = optionalToken.orElseThrow(
			() -> new UnauthorizedException("Invalid refresh token"));

		boolean valid = _authDomainService.isRefreshTokenValid(refreshToken);

		if (!valid) {
			throw new UnauthorizedException("Refresh token expired or revoked");
		}

		return refreshToken;
	}

	private AuthSession _getValidSession(UUID sessionId)
		throws UnauthorizedException {

		Optional<AuthSession> optionalSession = _saveSessionPort.findById(
			sessionId);

		AuthSession session = optionalSession.orElseThrow(
			() -> new UnauthorizedException("Invalid session for token"));

		boolean valid = _authDomainService.isSessionValid(session);

		if (!valid) {
			throw new UnauthorizedException("Session expired or revoked");
		}

		return session;
	}

	private User _getValidUser(UUID userId) throws UnauthorizedException {
		Optional<User> optionalUser = _loadUserPort.findById(userId);

		User user = optionalUser.orElseThrow(
			() -> new UnauthorizedException("User not found for token"));

		Boolean deleted = user.getDeleted();

		if ((deleted != null) && deleted) {
			throw new UnauthorizedException("User account is deactivated");
		}

		return user;
	}

	private static final String _PASSWORD_CREDENTIAL_TYPE = "PASSWORD";

	private final AuthDomainService _authDomainService;
	private final JwtTokenService _jwtTokenService;
	private final LoadUserPort _loadUserPort;
	private final RefreshTokenPort _refreshTokenPort;
	private final SaveSessionPort _saveSessionPort;
	private final TokenBlacklistPort _tokenBlacklistPort;

}