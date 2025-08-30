package com.easybase.security.application;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybase.common.exception.UnauthorizedException;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthApplicationService implements AuthUseCase {

	private static final String PASSWORD_CREDENTIAL_TYPE = "PASSWORD";

	@Override
	@Transactional
	public TokenResponse login(UUID tenantId, String email, String password,
			String userAgent, String ipAddress) throws UnauthorizedException {
		try {
			User user = _getAuthenticatedUser(email, password, tenantId);

			AuthSession session = _createAndSaveSession(user, userAgent,
					ipAddress);

			RefreshToken refreshToken = _createAndSaveRefreshToken(user,
					session);

			return _buildTokenResponse(session, refreshToken, user);
		} catch (Exception e) {

			throw new UnauthorizedException("Invalid credentials provided.", e);
		}
	}

	@Override
	@Transactional
	public TokenResponse refresh(String refreshTokenId, String userAgent,
			String ipAddress) throws UnauthorizedException {

		try {
			RefreshToken refreshToken = _getValidRefreshToken(refreshTokenId);

			User user = _getValidUser(refreshToken.getUserId());

			AuthSession session = _getValidSession(refreshToken.getSessionId());

			_tokenBlacklistPort.blacklistToken(session.getSessionToken(),
					session.getExpiresAt());

			String newSessionToken = _generateSessionToken(user.getId(),
					user.getTenant().getId(), user.getEmail());

			AuthSession refreshedSession = _authDomainService
					.refreshSession(session, newSessionToken);

			refreshedSession = _saveSessionPort.save(refreshedSession);

			_refreshTokenPort
					.save(_authDomainService.revokeRefreshToken(refreshToken));

			RefreshToken newRefreshToken = _authDomainService
					.createRefreshToken(user.getId(), user.getTenant().getId(),
							refreshedSession.getId(),
							refreshToken.getId().toString());
			newRefreshToken = _refreshTokenPort.save(newRefreshToken);

			return _buildTokenResponse(refreshedSession, newRefreshToken, user);
		} catch (Exception e) {

			throw new UnauthorizedException("Token refresh failed.", e);
		}
	}

	@Override
	@Transactional
	public void revoke(String sessionToken) {
		AuthSession session = _saveSessionPort.findBySessionToken(sessionToken)
				.orElseThrow(() -> new UnauthorizedException(
						"Invalid session token"));

		_saveSessionPort.save(_authDomainService.revokeSession(session));

		_refreshTokenPort.revokeBySessionId(session.getId());

		_tokenBlacklistPort.blacklistToken(sessionToken,
				session.getExpiresAt());

		log.info("Session revoked: sessionId={}", session.getId());
	}

	@Override
	@Transactional
	public void revokeAll(UUID userId, UUID tenantId) {
		_saveSessionPort.revokeAllByUserIdAndTenantId(userId, tenantId);

		_refreshTokenPort.revokeAllByUserIdAndTenantId(userId, tenantId);

		log.info("All sessions revoked for userId={}, tenantId={}", userId,
				tenantId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean validateToken(String sessionToken) {
		if (_tokenBlacklistPort.isTokenBlacklisted(sessionToken)) {
			return false;
		}

		return _saveSessionPort.findBySessionToken(sessionToken)
				.map(_authDomainService::isSessionValid).orElse(false);
	}

	@Override
	@Transactional(readOnly = true)
	public UUID getCurrentUserId(String sessionToken)
			throws UnauthorizedException {
		return _getSession(sessionToken).getUserId();
	}

	@Override
	@Transactional(readOnly = true)
	public UUID getCurrentTenantId(String sessionToken)
			throws UnauthorizedException {
		return _getSession(sessionToken).getTenantId();
	}

	private User _getAuthenticatedUser(String email, String password,
			UUID tenantId) throws UnauthorizedException {

		String sanitizedEmail = email.trim().toLowerCase();

		User user = _loadUserPort
				.findByEmailAndTenantId(sanitizedEmail, tenantId)
				.orElseThrow(() -> new UnauthorizedException("User not found"));

		if (user.getIsDeleted()) {
			throw new UnauthorizedException("User account is deleted");
		}

		UserCredential credential = _loadUserPort
				.findCredentialByUserId(user.getId(), PASSWORD_CREDENTIAL_TYPE)
				.orElseThrow(() -> new UnauthorizedException(
						"Password credential not found"));

		String hashedPassword = credential.getPasswordHash();

		if (hashedPassword == null
				|| !_loadUserPort.verifyPassword(password, hashedPassword)) {
			throw new UnauthorizedException("Password mismatch");
		}

		return user;
	}

	private AuthSession _createAndSaveSession(User user, String userAgent,
			String ipAddress) {

		String sessionToken = _generateSessionToken(user.getId(),
				user.getTenant().getId(), user.getEmail());

		AuthSession session = _authDomainService.createSession(user.getId(),
				user.getTenant().getId(), sessionToken, userAgent, ipAddress);

		return _saveSessionPort.save(session);
	}

	private RefreshToken _createAndSaveRefreshToken(User user,
			AuthSession session) {

		RefreshToken refreshToken = _authDomainService.createRefreshToken(
				user.getId(), user.getTenant().getId(), session.getId(), null);

		return _refreshTokenPort.save(refreshToken);
	}

	private String _generateSessionToken(UUID userId, UUID tenantId,
			String email) {
		return _jwtTokenService.generateAccessToken(userId, tenantId, email);
	}

	private RefreshToken _getValidRefreshToken(String tokenString)
			throws UnauthorizedException {
		UUID refreshTokenId;

		try {
			refreshTokenId = UUID.fromString(tokenString.trim());
		} catch (IllegalArgumentException iae) {
			throw new UnauthorizedException("Invalid refresh token format",
					iae);
		}

		RefreshToken refreshToken = _refreshTokenPort.findById(refreshTokenId)
				.orElseThrow(() -> new UnauthorizedException(
						"Invalid refresh token"));

		if (!_authDomainService.isRefreshTokenValid(refreshToken)) {
			throw new UnauthorizedException("Refresh token expired or revoked");
		}
		return refreshToken;
	}

	private User _getValidUser(UUID userId) throws UnauthorizedException {
		User user = _loadUserPort.findById(userId).orElseThrow(
				() -> new UnauthorizedException("User not found for token"));

		if (user.getIsDeleted() != null && user.getIsDeleted()) {
			throw new UnauthorizedException("User account is deactivated");
		}
		return user;
	}

	private AuthSession _getValidSession(UUID sessionId)
			throws UnauthorizedException {
		AuthSession session = _saveSessionPort.findById(sessionId).orElseThrow(
				() -> new UnauthorizedException("Invalid session for token"));

		if (!_authDomainService.isSessionValid(session)) {
			throw new UnauthorizedException("Session expired or revoked");
		}
		return session;
	}

	private AuthSession _getSession(String sessionToken)
			throws UnauthorizedException {
		return _saveSessionPort.findBySessionToken(sessionToken)
				.filter(_authDomainService::isSessionValid)
				.orElseThrow(() -> new UnauthorizedException(
						"Invalid or expired session token"));
	}

	private TokenResponse _buildTokenResponse(AuthSession session,
			RefreshToken refreshToken, User user) {
		long expiresInSeconds = Duration
				.between(Instant.now(), session.getExpiresAt()).getSeconds();

		TokenResponse tokenResponse = new TokenResponse();

		tokenResponse.setAccessToken(session.getSessionToken());
		tokenResponse.setRefreshToken(refreshToken.getId().toString());
		tokenResponse.setTokenType("Bearer");
		tokenResponse.setExpiresIn(expiresInSeconds);
		tokenResponse.setExpiresAt(session.getExpiresAt());
		tokenResponse.setUserId(session.getUserId());
		tokenResponse.setTenantId(session.getTenantId());
		tokenResponse.setUserEmail(user.getEmail());
		tokenResponse.setUserDisplayName(user.getDisplayName());

		return tokenResponse;
	}

	private final AuthDomainService _authDomainService;

	private final LoadUserPort _loadUserPort;

	private final SaveSessionPort _saveSessionPort;

	private final RefreshTokenPort _refreshTokenPort;

	private final TokenBlacklistPort _tokenBlacklistPort;

	private final JwtTokenService _jwtTokenService;
}
