/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.provider;

import com.easybase.context.api.domain.CorrelationIds;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.context.api.domain.TenantInfo;
import com.easybase.context.api.domain.UserInfo;
import com.easybase.context.api.port.ContextProvider;
import com.easybase.context.api.port.TenantInfoResolver;
import com.easybase.context.api.port.UserInfoResolver;
import com.easybase.context.core.impl.ServiceContextImpl;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.api.exception.AuthenticationException;
import com.easybase.security.api.service.AuthenticationFacade;

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
		AuthenticatedPrincipalData principalData = null;

		try {
			String token = _extractTokenFromRequest(request);

			if (token != null) {
				principalData = _authenticationFacade.authenticateByToken(
					token);
			}
		}
		catch (AuthenticationException authenticationException) {
			log.debug(
				"Authentication failed: {}",
				authenticationException.getMessage());
		}

		UserInfo user = _resolveUser(principalData);
		TenantInfo tenant = _resolveTenant(principalData);
		CorrelationIds correlation = _buildCorrelation(request);

		return ServiceContextImpl.builder(
		).user(
			user
		).tenant(
			tenant
		).correlation(
			correlation
		).issuedAt(
			(principalData != null) ? principalData.getIssuedAt() : null
		).expiresAt(
			(principalData != null) ? principalData.getExpiresAt() : null
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

	private String _extractTokenFromRequest(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if ((authHeader != null) && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}

		return null;
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

	private TenantInfo _resolveTenant(
		AuthenticatedPrincipalData principalData) {

		if (principalData == null) {
			return TenantInfo.publicTenant();
		}

		try {
			return _tenantInfoResolver.resolve(principalData.getTenantId());
		}
		catch (Exception exception) {
			log.warn(
				"Failed to resolve tenant {}, using public tenant",
				principalData.getTenantId(), exception);

			return TenantInfo.publicTenant();
		}
	}

	private UserInfo _resolveUser(AuthenticatedPrincipalData principalData) {
		if (principalData == null) {
			return UserInfo.anonymous();
		}

		try {
			return _userInfoResolver.resolve(principalData.getUserId());
		}
		catch (Exception exception) {
			log.warn(
				"Failed to resolve user {}, using anonymous",
				principalData.getUserId(), exception);

			return UserInfo.anonymous();
		}
	}

	private final AuthenticationFacade _authenticationFacade;
	private final TenantInfoResolver _tenantInfoResolver;
	private final UserInfoResolver _userInfoResolver;

}