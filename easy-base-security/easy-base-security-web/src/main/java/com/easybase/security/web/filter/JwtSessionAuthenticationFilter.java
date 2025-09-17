/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.web.filter;

import com.easybase.security.api.constants.SecurityConstants;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.api.dto.Session;
import com.easybase.security.api.dto.TokenClaims;
import com.easybase.security.api.dto.TokenValidationResult;
import com.easybase.security.api.service.SessionService;
import com.easybase.security.api.service.TokenService;
import com.easybase.security.core.service.ServiceContextBinding;
import com.easybase.security.web.authentication.JwtAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.time.Instant;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter that validates JWT tokens and creates authentication
 * context for each request.
 *
 * <p>This filter handles token extraction, validation, session verification,
 * and principal binding for secure endpoints.</p>
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class JwtSessionAuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws IOException {

		try {
			String token = _extractTokenFromRequest(request);

			if (token == null) {
				filterChain.doFilter(request, response);

				return;
			}

			TokenValidationResult validationResult =
				_tokenService.validateAccessToken(token);

			if (!validationResult.isValid()) {
				Optional<String> reasonOptional = validationResult.getReason();

				String reason = reasonOptional.orElse("Unknown");

				_sendUnauthorizedResponse(response, reason);

				return;
			}

			Optional<TokenClaims> claimsOptional = validationResult.getClaims();

			TokenClaims claims = claimsOptional.orElseThrow();

			Optional<Session> session = _sessionService.getSession(
				claims.getSessionId());

			if (session.isEmpty()) {
				_sendUnauthorizedResponse(response, "Session expired");

				return;
			}

			Session activeSession = session.get();

			if (activeSession.isRevoked()) {
				_sendUnauthorizedResponse(response, "Session revoked");

				return;
			}

			UUID tokenTenantId = claims.getTenantId();
			UUID sessionTenantId = activeSession.getTenantId();

			if (!tokenTenantId.equals(sessionTenantId)) {
				_sendUnauthorizedResponse(response, "Tenant mismatch");

				return;
			}

			_sessionService.touchSession(claims.getSessionId());

			AuthenticatedPrincipalData principal = _createPrincipalData(
				claims, activeSession, request);

			SecurityContext securityContext =
				SecurityContextHolder.getContext();

			securityContext.setAuthentication(_createAuthentication(principal));

			_serviceContextBinding.bind(principal);

			filterChain.doFilter(request, response);
		}
		catch (Exception exception) {
			_sendUnauthorizedResponse(response, "Authentication failed");
		}
		finally {
			_serviceContextBinding.clear();
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();

		if (path.startsWith("/api/auth/login") ||
			path.startsWith("/.well-known/") || path.startsWith("/actuator/") ||
			path.startsWith("/swagger-") || path.startsWith("/v3/api-docs")) {

			return true;
		}

		return false;
	}

	private Authentication _createAuthentication(
		AuthenticatedPrincipalData principal) {

		List<String> roles = principal.getAuthorities();

		Stream<String> rolesStream = roles.stream();

		List<SimpleGrantedAuthority> authorities = rolesStream.map(
			SimpleGrantedAuthority::new
		).toList();

		return new JwtAuthenticationToken(principal, authorities);
	}

	private AuthenticatedPrincipalData _createPrincipalData(
		TokenClaims claims, Session session, HttpServletRequest request) {

		AuthenticatedPrincipalData principalData =
			new AuthenticatedPrincipalData();

		principalData.setUserId(claims.getUserId());
		principalData.setTenantId(claims.getTenantId());
		principalData.setSessionId(claims.getSessionId());
		principalData.setAuthorities(
			(claims.getRoles() != null) ? claims.getRoles() :
				Collections.emptyList());
		principalData.setMetadata(session.getMetadata());
		principalData.setIssuedAt(claims.getIssuedAt());
		principalData.setExpiresAt(claims.getExpiresAt());
		principalData.setClientIp(_getClientIp(request));
		principalData.setUserAgent(request.getHeader("User-Agent"));

		return principalData;
	}

	private String _extractTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(
			SecurityConstants.AUTHORIZATION_HEADER);

		if (StringUtils.hasText(bearerToken) &&
			bearerToken.startsWith(SecurityConstants.BEARER_PREFIX)) {

			return bearerToken.substring(
				SecurityConstants.BEARER_PREFIX.length());
		}

		return null;
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

	private void _sendUnauthorizedResponse(
			HttpServletResponse response, String message)
		throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String timestamp = String.valueOf(Instant.now());

		String jsonResponse = String.format(
			"{\"timestamp\":\"%s\",\"status\":401," +
				"\"error\":\"Unauthorized\",\"message\":\"%s\"}",
			timestamp, message);

		response.getWriter(
		).write(
			jsonResponse
		);
	}

	private final ServiceContextBinding _serviceContextBinding;
	private final SessionService _sessionService;
	private final TokenService _tokenService;

}