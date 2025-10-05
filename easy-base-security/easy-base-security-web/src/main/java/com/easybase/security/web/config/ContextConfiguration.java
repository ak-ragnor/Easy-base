/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.web.config;

import com.easybase.context.api.domain.PermissionContext;
import com.easybase.context.api.domain.ServiceContext;
import com.easybase.security.core.service.PermissionContextBinding;
import com.easybase.security.core.service.ServiceContextBinding;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Spring configuration for ServiceContext and PermissionContext system.
 * Configures request-scoped beans that can be injected into any service
 * for accessing current user/tenant/permission information.
 *
 * @author Akhash R
 */
@Configuration
@RequiredArgsConstructor
public class ContextConfiguration {

	/**
	 * Provides request-scoped ServiceContext bean.
	 * The context is retrieved from thread-local storage via ServiceContextBinding.
	 *
	 * @return the ServiceContext for the current request
	 */
	@Bean
	@RequestScope
	public ServiceContext serviceContext() {
		ServiceContext context = _serviceContextBinding.getCurrentServiceContext();

		if (context == null) {
			throw new IllegalStateException(
				"ServiceContext not found. Ensure JwtSessionAuthenticationFilter authenticated the request.");
		}

		return context;
	}

	/**
	 * Provides request-scoped PermissionContext bean.
	 * The context is retrieved from thread-local storage via PermissionContextBinding.
	 *
	 * @return the PermissionContext for the current request
	 */
	@Bean
	@RequestScope
	public PermissionContext permissionContext() {
		PermissionContext context = _permissionContextBinding.getCurrentPermissionContext();

		if (context == null) {
			throw new IllegalStateException(
				"PermissionContext not found. Ensure JwtSessionAuthenticationFilter authenticated the request.");
		}

		return context;
	}

	private final PermissionContextBinding _permissionContextBinding;
	private final ServiceContextBinding _serviceContextBinding;

}
