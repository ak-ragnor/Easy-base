/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.config;

import com.easybase.common.api.context.ServiceContext;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Spring configuration for ServiceContext system.
 * Configures request-scoped ServiceContext bean that can be injected
 * into any service for accessing current user/tenant information.
 *
 * @author Akhash R
 */
@Configuration
public class ContextConfiguration {

	/**
	 * Provides request-scoped ServiceContext bean.
	 * The context is populated by ServiceContextFilter and stored as
	 * a request attribute, which this bean retrieves.
	 *
	 * @param requestFactory factory for getting current HTTP request
	 * @return the ServiceContext for the current request
	 */
	@Bean
	@RequestScope
	public ServiceContext serviceContext(
		ObjectFactory<HttpServletRequest> requestFactory) {

		HttpServletRequest request = requestFactory.getObject();

		ServiceContext context = (ServiceContext)request.getAttribute(
			ServiceContextFilter.CONTEXT_ATTRIBUTE);

		if (context == null) {
			throw new IllegalStateException(
				"ServiceContext not found in request. Ensure ServiceContextFilter is configured properly.");
		}

		return context;
	}

}