/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.web.controller;

import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.api.dto.LoginRequest;
import com.easybase.security.api.dto.LogoutRequest;
import com.easybase.security.api.dto.RefreshTokenRequest;
import com.easybase.security.api.dto.Session;
import com.easybase.security.api.dto.TokenResponse;
import com.easybase.security.api.exception.AuthenticationException;
import com.easybase.security.api.service.AuthenticationFacade;
import com.easybase.security.api.service.SessionService;
import com.easybase.security.api.service.TokenService;
import com.easybase.security.core.service.PermissionContextBinding;
import com.easybase.security.core.service.ServiceContextBinding;
import com.easybase.security.jwt.config.JwtProperties;
import com.easybase.security.web.authentication.JwtAuthenticationToken;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;

import java.time.Duration;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that handles authentication-related operations including
 * login, logout, token refresh, and session management.
 *
 * <p>This controller provides endpoints for user authentication, session listing,
 * and token operations with proper security validation.</p>
 *
 * @author Akhash
 */
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

	@GetMapping("/sessions")
	public ResponseEntity<List<Session>> listSessions() {
		JwtAuthenticationToken jwtAuth = _getJwtAuthentication();

		if (jwtAuth == null) {
			ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(401);

			return bodyBuilder.build();
		}

		AuthenticatedPrincipalData principal = jwtAuth.getPrincipal();

		List<Session> sessions = _sessionService.listSessions(
			principal.getUserId(), principal.getTenantId());

		return ResponseEntity.ok(sessions);
	}

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(
		@RequestBody @Valid LoginRequest loginRequest,
		HttpServletRequest request) {

		loginRequest.setClientIp(_getClientIp(request));
		loginRequest.setUserAgent(request.getHeader("User-Agent"));

		AuthenticatedPrincipalData principal =
			_authenticationFacade.authenticateCredentials(loginRequest);

		Duration ttlSeconds = _jwtProperties.getAccessTokenTtl();

		String accessToken = _tokenService.generateAccessToken(
			principal, _jwtProperties.getAccessTokenTtl());
		String refreshToken = _tokenService.generateRefreshToken(
			principal.getSessionId());

		TokenResponse response = TokenResponse.of(
			accessToken, refreshToken, ttlSeconds.toSeconds(),
			principal.getSessionId());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(
		@RequestBody(required = false) LogoutRequest logoutRequest) {

		String sessionId = null;

		if ((logoutRequest != null) &&
			StringUtils.hasText(logoutRequest.getSessionId())) {

			sessionId = logoutRequest.getSessionId();
		}
		else {
			JwtAuthenticationToken jwtAuth = _getJwtAuthentication();

			if (jwtAuth != null) {
				AuthenticatedPrincipalData principal = jwtAuth.getPrincipal();

				sessionId = principal.getSessionId();
			}
		}

		if (sessionId != null) {
			_authenticationFacade.logout(sessionId);
		}

		SecurityContextHolder.clearContext();
		_serviceContextBinding.clear();
		_permissionContextBinding.clear();

		return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
	}

	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refresh(
		@RequestBody @Valid RefreshTokenRequest refreshRequest) {

		try {
			AuthenticatedPrincipalData principal =
				_authenticationFacade.refreshAuthentication(
					refreshRequest.getRefreshToken());

			String accessToken = _tokenService.generateAccessToken(
				principal, _jwtProperties.getAccessTokenTtl());

			String refreshToken;

			if (_jwtProperties.isRotateRefreshTokens()) {
				_authenticationFacade.revokeRefreshToken(
					refreshRequest.getRefreshToken());
				refreshToken = _tokenService.generateRefreshToken(
					principal.getSessionId());
			}
			else {
				refreshToken = refreshRequest.getRefreshToken();
			}

			Duration ttlSeconds = _jwtProperties.getAccessTokenTtl();

			TokenResponse response = TokenResponse.of(
				accessToken, refreshToken, ttlSeconds.toSeconds(),
				principal.getSessionId());

			return ResponseEntity.ok(response);
		}
		catch (AuthenticationException authenticationException) {
			ResponseEntity.BodyBuilder responseEntityBuilder =
				ResponseEntity.status(401);

			return responseEntityBuilder.build();
		}
	}

	@PostMapping("/sessions/revoke-all")
	public ResponseEntity<Map<String, String>> revokeAllSessions() {
		JwtAuthenticationToken jwtAuth = _getJwtAuthentication();

		if (jwtAuth == null) {
			ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(401);

			return bodyBuilder.build();
		}

		AuthenticatedPrincipalData principal = jwtAuth.getPrincipal();

		_sessionService.revokeSessionsForUser(
			principal.getUserId(), principal.getTenantId());

		return ResponseEntity.ok(
			Map.of("message", "All sessions revoked successfully"));
	}

	@DeleteMapping("/sessions/{sessionId}")
	public ResponseEntity<Map<String, String>> revokeSession(
		@PathVariable String sessionId) {

		JwtAuthenticationToken jwtAuth = _getJwtAuthentication();

		if (jwtAuth == null) {
			ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(401);

			return bodyBuilder.build();
		}

		AuthenticatedPrincipalData principal = jwtAuth.getPrincipal();

		// Verify the session belongs to the current user

		List<Session> userSessions = _sessionService.listSessions(
			principal.getUserId(), principal.getTenantId());

		Stream<Session> sessionStream = userSessions.stream();

		boolean sessionBelongsToUser = sessionStream.anyMatch(
			session -> session.getSessionId(
			).equals(
				sessionId
			));

		if (!sessionBelongsToUser) {
			ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(401);

			return bodyBuilder.body(
				Map.of("error", "Session does not belong to current user"));
		}

		_authenticationFacade.logout(sessionId);

		return ResponseEntity.ok(
			Map.of("message", "Session revoked successfully"));
	}

	private String _getClientIp(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");

		if (StringUtils.hasText(xForwardedFor)) {
			return xForwardedFor.split(",")[0].trim();
		}

		String xRealIp = request.getHeader("X-Real-IP");

		if (StringUtils.hasText(xRealIp)) {
			return xRealIp;
		}

		return request.getRemoteAddr();
	}

	private JwtAuthenticationToken _getJwtAuthentication() {
		SecurityContext securityContext = SecurityContextHolder.getContext();

		Authentication auth = securityContext.getAuthentication();

		if (auth instanceof JwtAuthenticationToken) {
			return (JwtAuthenticationToken)auth;
		}

		return null;
	}

	private final AuthenticationFacade _authenticationFacade;
	private final JwtProperties _jwtProperties;
	private final ServiceContextBinding _serviceContextBinding;
	private final PermissionContextBinding _permissionContextBinding;
	private final SessionService _sessionService;
	private final TokenService _tokenService;

}