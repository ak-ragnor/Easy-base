/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.provider;

import com.easybase.common.exception.ExpiredTokenException;
import com.easybase.common.exception.InvalidTokenException;
import com.easybase.context.api.domain.CorrelationIds;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.context.api.domain.TenantInfo;
import com.easybase.context.api.domain.UserInfo;
import com.easybase.context.api.port.ContextProvider;
import com.easybase.context.api.port.TenantInfoResolver;
import com.easybase.context.api.port.UserInfoResolver;
import com.easybase.context.core.impl.ServiceContextImpl;
import com.easybase.security.domain.model.AuthResult;
import com.easybase.security.domain.port.in.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * Default implementation of ContextProvider that uses authentication facade
 * and resolvers to build complete ServiceContext from HTTP requests.
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultContextProvider implements ContextProvider {

	@Override
	public ServiceContext build(HttpServletRequest request) {
		AuthResult authResult = null;

		try {
			authResult = _authenticationService.authenticate(request);
		}
		catch (ExpiredTokenException | InvalidTokenException exception) {
			log.debug("Authentication failed: {}", exception.getMessage());
		}

		UserInfo user = _resolveUser(authResult);
		TenantInfo tenant = _resolveTenant(authResult);
		CorrelationIds correlation = _buildCorrelation(request);

		return ServiceContextImpl.builder(
		).user(
			user
		).tenant(
			tenant
		).correlation(
			correlation
		).issuedAt(
			(authResult != null) ? authResult.getIssuedAt() : null
		).expiresAt(
			(authResult != null) ? authResult.getExpiresAt() : null
		).clientIp(
			_getClientIpAddress(request)
		).userAgent(
			request.getHeader("User-Agent")
		).build();
	}

	private CorrelationIds _buildCorrelation(HttpServletRequest request) {
		String requestId = request.getHeader("X-Request-Id");
		String sessionId = request.getHeader("X-Session-Id");
		String traceId = request.getHeader("X-Trace-Id");

		if (requestId == null) {
			return CorrelationIds.create(sessionId, traceId);
		}

		return new CorrelationIds(requestId, sessionId, traceId);
	}

	private String _getClientIpAddress(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");

		if ((xForwardedFor != null) && !xForwardedFor.isEmpty() &&
			!"unknown".equalsIgnoreCase(xForwardedFor)) {

			return xForwardedFor.split(",")[0].trim();
		}

		String xRealIp = request.getHeader("X-Real-IP");

		if ((xRealIp != null) && !xRealIp.isEmpty() &&
			!"unknown".equalsIgnoreCase(xRealIp)) {

			return xRealIp;
		}

		return request.getRemoteAddr();
	}

	private TenantInfo _resolveTenant(AuthResult authResult) {
		if (authResult == null) {
			return TenantInfo.publicTenant();
		}

		try {
			return _tenantInfoResolver.resolve(authResult.getTenantId());
		}
		catch (Exception exception) {
			log.warn(
				"Failed to resolve tenant {}, using public tenant",
				authResult.getTenantId(), exception);

			return TenantInfo.publicTenant();
		}
	}

	private UserInfo _resolveUser(AuthResult authResult) {
		if (authResult == null) {
			return UserInfo.anonymous();
		}

		try {
			return _userInfoResolver.resolve(authResult.getUserId());
		}
		catch (Exception exception) {
			log.warn(
				"Failed to resolve user {}, using anonymous",
				authResult.getUserId(), exception);

			return UserInfo.anonymous();
		}
	}

	private final AuthenticationService _authenticationService;
	private final TenantInfoResolver _tenantInfoResolver;
	private final UserInfoResolver _userInfoResolver;

}