package com.easybase.security.application;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easybase.common.exception.ResourceNotFoundException;
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
import com.easybase.security.dto.LoginRequest;
import com.easybase.security.dto.RefreshTokenRequest;
import com.easybase.security.dto.TokenResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthApplicationService implements AuthUseCase {

	@Override
	@Transactional
	public TokenResponse login(LoginRequest request, String userAgent,
			String ipAddress) {
		User user = _loadUserPort
				.findByEmailAndTenantId(request.getEmail(),
						request.getTenantId())
				.orElseThrow(
						() -> new UnauthorizedException("Invalid credentials"));

		UserCredential credential = _loadUserPort
				.findCredentialByUserId(user.getId(), "PASSWORD").orElseThrow(
						() -> new UnauthorizedException("Invalid credentials"));

		String hashedPassword = credential.getPasswordHash();

		if (hashedPassword == null || !_loadUserPort
				.verifyPassword(request.getPassword(), hashedPassword)) {
			throw new UnauthorizedException("Invalid credentials");
		}

		String sessionToken = _generateSessionToken(user.getId(),
				user.getTenant().getId(), user.getEmail());

		AuthSession session = _authDomainService.createSession(user.getId(),
				user.getTenant().getId(), sessionToken, userAgent, ipAddress);

		session = _saveSessionPort.save(session);

		RefreshToken refreshToken = _authDomainService.createRefreshToken(
				user.getId(), user.getTenant().getId(), session.getId(), null);

		refreshToken = _refreshTokenPort.save(refreshToken);

		log.info("User logged in successfully: userId={}, tenantId={}",
				user.getId(), user.getTenant().getId());

		return _buildTokenResponse(session, refreshToken, user);
	}

	@Override
	@Transactional
	public TokenResponse refresh(RefreshTokenRequest request, String userAgent,
			String ipAddress) {
		UUID refreshTokenId;
		try {
			refreshTokenId = UUID.fromString(request.getRefreshToken());
		} catch (IllegalArgumentException e) {
			throw new UnauthorizedException("Invalid refresh token format");
		}

		RefreshToken refreshToken = _refreshTokenPort.findById(refreshTokenId)
				.orElseThrow(() -> new UnauthorizedException(
						"Invalid refresh token"));

		if (!_authDomainService.isRefreshTokenValid(refreshToken)) {
			throw new UnauthorizedException("Refresh token expired or revoked");
		}

		AuthSession session = _saveSessionPort
				.findById(refreshToken.getSessionId()).orElseThrow(
						() -> new UnauthorizedException("Invalid session"));

		User user = _loadUserPort.findById(refreshToken.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "id",
						refreshToken.getUserId()));

		_tokenBlacklistPort.blacklistToken(session.getSessionToken(),
				session.getExpiresAt());

		String newSessionToken = _generateSessionToken(refreshToken.getUserId(),
				refreshToken.getTenantId(), user.getEmail());

		AuthSession refreshedSession = _authDomainService
				.refreshSession(session, newSessionToken);

		refreshedSession = _saveSessionPort.save(refreshedSession);

		RefreshToken oldRefreshToken = _authDomainService
				.revokeRefreshToken(refreshToken);
		_refreshTokenPort.save(oldRefreshToken);

		RefreshToken newRefreshToken = _authDomainService.createRefreshToken(
				refreshToken.getUserId(), refreshToken.getTenantId(),
				refreshedSession.getId(), refreshToken.getId().toString());
		newRefreshToken = _refreshTokenPort.save(newRefreshToken);

		log.info("Token refreshed successfully: userId={}, tenantId={}",
				refreshToken.getUserId(), refreshToken.getTenantId());

		return _buildTokenResponse(refreshedSession, newRefreshToken, user);
	}

	@Override
	@Transactional
	public void revoke(String sessionToken) {
		AuthSession session = _saveSessionPort.findBySessionToken(sessionToken)
				.orElseThrow(() -> new UnauthorizedException(
						"Invalid session token"));

		AuthSession revokedSession = _authDomainService.revokeSession(session);

		_saveSessionPort.save(revokedSession);

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
	public UUID getCurrentUserId(String sessionToken) {
		return _saveSessionPort.findBySessionToken(sessionToken)
				.filter(_authDomainService::isSessionValid)
				.map(AuthSession::getUserId)
				.orElseThrow(() -> new UnauthorizedException(
						"Invalid or expired session token"));
	}

	@Override
	@Transactional(readOnly = true)
	public UUID getCurrentTenantId(String sessionToken) {
		return _saveSessionPort.findBySessionToken(sessionToken)
				.filter(_authDomainService::isSessionValid)
				.map(AuthSession::getTenantId)
				.orElseThrow(() -> new UnauthorizedException(
						"Invalid or expired session token"));
	}

	private String _generateSessionToken(UUID userId, UUID tenantId,
			String email) {

		return _jwtTokenService.generateAccessToken(userId, tenantId, email);
	}

	private TokenResponse _buildTokenResponse(AuthSession session,
			RefreshToken refreshToken, User user) {
		long expiresInSeconds = Duration
				.between(Instant.now(), session.getExpiresAt()).getSeconds();

		return TokenResponse.builder().accessToken(session.getSessionToken())
				.refreshToken(refreshToken.getId().toString())
				.tokenType("Bearer").expiresIn(expiresInSeconds)
				.expiresAt(session.getExpiresAt()).userId(session.getUserId())
				.tenantId(session.getTenantId()).userEmail(user.getEmail())
				.userDisplayName(user.getDisplayName()).build();
	}

	private final AuthDomainService _authDomainService;

	private final LoadUserPort _loadUserPort;

	private final SaveSessionPort _saveSessionPort;

	private final RefreshTokenPort _refreshTokenPort;

	private final TokenBlacklistPort _tokenBlacklistPort;

	private final JwtTokenService _jwtTokenService;
}
