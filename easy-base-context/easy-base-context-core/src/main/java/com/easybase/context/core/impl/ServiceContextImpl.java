/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.impl;

import com.easybase.context.api.domain.CorrelationIds;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.context.api.domain.TenantInfo;
import com.easybase.context.api.domain.UserInfo;

import java.time.Instant;

import java.util.Optional;

import lombok.Getter;

/**
 * Implementation of ServiceContext using lightweight DTOs with lazy loading.
 * All expensive operations are deferred until actually accessed.
 *
 * @author Akhash R
 */
@Getter
public class ServiceContextImpl implements ServiceContext {

	/**
	 * Creates a builder for ServiceContextImpl.
	 *
	 * @return a new ServiceContextBuilder
	 */
	public static ServiceContextBuilder builder() {
		return new ServiceContextBuilder();
	}

	@Override
	public Optional<String> clientIp() {
		return _clientIp;
	}

	@Override
	public CorrelationIds correlation() {
		return _correlation;
	}

	@Override
	public Instant expiresAt() {
		return _expiresAt;
	}

	@Override
	public Instant issuedAt() {
		return _issuedAt;
	}

	@Override
	public TenantInfo tenant() {
		return _tenant;
	}

	@Override
	public UserInfo user() {
		return _user;
	}

	@Override
	public Optional<String> userAgent() {
		return _userAgent;
	}

	/**
	 * Builder class for creating ServiceContext instances.
	 */
	public static class ServiceContextBuilder {

		/**
		 * Builds the ServiceContext instance.
		 *
		 * @return the created ServiceContext
		 */
		public ServiceContext build() {
			ServiceContextImpl context = new ServiceContextImpl();

			context._clientIp = _clientIp;
			context._correlation = _correlation;
			context._expiresAt = _expiresAt;
			context._issuedAt = _issuedAt;
			context._tenant = _tenant;
			context._user = _user;
			context._userAgent = _userAgent;

			return context;
		}

		/**
		 * Sets the client IP address.
		 *
		 * @param clientIp the client IP address
		 * @return this builder
		 */
		public ServiceContextBuilder clientIp(String clientIp) {
			_clientIp = Optional.ofNullable(clientIp);

			return this;
		}

		/**
		 * Sets the correlation IDs.
		 *
		 * @param correlation the correlation IDs
		 * @return this builder
		 */
		public ServiceContextBuilder correlation(CorrelationIds correlation) {
			_correlation = correlation;

			return this;
		}

		/**
		 * Sets the token expiration time.
		 *
		 * @param expiresAt when the token expires
		 * @return this builder
		 */
		public ServiceContextBuilder expiresAt(Instant expiresAt) {
			_expiresAt = expiresAt;

			return this;
		}

		/**
		 * Sets the token issued time.
		 *
		 * @param issuedAt when the token was issued
		 * @return this builder
		 */
		public ServiceContextBuilder issuedAt(Instant issuedAt) {
			_issuedAt = issuedAt;

			return this;
		}


		/**
		 * Sets the tenant information.
		 *
		 * @param tenant the tenant info
		 * @return this builder
		 */
		public ServiceContextBuilder tenant(TenantInfo tenant) {
			_tenant = tenant;

			return this;
		}

		/**
		 * Sets the user information.
		 *
		 * @param user the user info
		 * @return this builder
		 */
		public ServiceContextBuilder user(UserInfo user) {
			_user = user;

			return this;
		}

		/**
		 * Sets the user agent.
		 *
		 * @param userAgent the user agent string
		 * @return this builder
		 */
		public ServiceContextBuilder userAgent(String userAgent) {
			_userAgent = Optional.ofNullable(userAgent);

			return this;
		}

		private Optional<String> _clientIp = Optional.empty();
		private CorrelationIds _correlation;
		private Instant _expiresAt;
		private Instant _issuedAt;
		private TenantInfo _tenant;
		private UserInfo _user;
		private Optional<String> _userAgent = Optional.empty();

	}

	private Optional<String> _clientIp;
	private CorrelationIds _correlation;
	private Instant _expiresAt;
	private Instant _issuedAt;
	private TenantInfo _tenant;
	private UserInfo _user;
	private Optional<String> _userAgent;

}