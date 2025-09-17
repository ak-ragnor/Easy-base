/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.core.context;

import com.easybase.context.api.domain.CorrelationIds;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.context.api.domain.TenantInfo;
import com.easybase.context.api.domain.UserInfo;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.core.service.ServiceContextBinding;

import java.time.Instant;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * Implementation of {@link ServiceContextBinding} that manages authentication
 * principal data in thread-local storage and provides service context creation.
 *
 * <p>This binding implementation ensures thread-safe storage of authentication
 * context and provides utilities for creating service contexts from principal data.</p>
 *
 * @author Akhash
 */
@Component
public class ServiceContextBindingImpl implements ServiceContextBinding {

	@Override
	public void bind(AuthenticatedPrincipalData principal) {
		principalHolder.set(principal);
	}

	@Override
	public void clear() {
		principalHolder.remove();
	}

	@Override
	public AuthenticatedPrincipalData fromCurrentContext() {
		return principalHolder.get();
	}

	@Override
	public ServiceContext fromPrincipal(AuthenticatedPrincipalData principal) {
		return new SecurityServiceContext(principal);
	}

	private final ThreadLocal<AuthenticatedPrincipalData> principalHolder =
		new ThreadLocal<>();

	private static class SecurityServiceContext implements ServiceContext {

		public SecurityServiceContext(AuthenticatedPrincipalData principal) {
			this.principal = principal;

			tenantInfo = _createTenantInfo(principal);
			userInfo = _createUserInfo(principal);
			correlationIds = _createCorrelationIds(principal);
		}

		@Override
		public Optional<String> clientIp() {
			return Optional.ofNullable(principal.getClientIp());
		}

		@Override
		public CorrelationIds correlation() {
			return correlationIds;
		}

		@Override
		public Instant expiresAt() {
			return principal.getExpiresAt();
		}

		@Override
		public Instant issuedAt() {
			return principal.getIssuedAt();
		}

		@Override
		public TenantInfo tenant() {
			return tenantInfo;
		}

		@Override
		public UserInfo user() {
			return userInfo;
		}

		@Override
		public Optional<String> userAgent() {
			return Optional.ofNullable(principal.getUserAgent());
		}

		private CorrelationIds _createCorrelationIds(
			AuthenticatedPrincipalData principal) {

			return new CorrelationIds(
				String.valueOf(UUID.randomUUID()), principal.getSessionId(),
				null);
		}

		private TenantInfo _createTenantInfo(
			AuthenticatedPrincipalData principal) {

			return new TenantInfo(
				principal.getTenantId(), true,
				() -> "Tenant-" + principal.getTenantId(), Map::of);
		}

		private UserInfo _createUserInfo(AuthenticatedPrincipalData principal) {
			return new UserInfo(
				principal.getUserId(), null, true,
				() -> "user-" + principal.getUserId(), List::of, List::of);
		}

		private final CorrelationIds correlationIds;
		private final AuthenticatedPrincipalData principal;
		private final TenantInfo tenantInfo;
		private final UserInfo userInfo;

	}

}