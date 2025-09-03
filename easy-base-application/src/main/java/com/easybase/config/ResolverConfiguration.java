/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.config;

import com.easybase.common.api.context.TenantInfoResolver;
import com.easybase.common.api.context.UserInfoResolver;
import com.easybase.config.context.CachingTenantInfoResolver;
import com.easybase.config.context.CachingUserInfoResolver;
import com.easybase.config.context.DefaultTenantInfoResolver;
import com.easybase.config.context.DefaultUserInfoResolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Spring configuration for resolver beans with proper caching setup.
 * Configures the resolver hierarchy using the new template patterns:
 * Caching -> AbstractDefault -> Repository.
 *
 * Note: The @Primary and @RequestScope annotations on the resolver classes
 * themselves handle the bean configuration, so explicit @Bean methods
 * are no longer needed. This configuration is kept for documentation
 * and potential future customization.
 *
 * @author Akhash R
 */
@Configuration
public class ResolverConfiguration {

	/**
	 * Primary tenant info resolver with request-scoped caching.
	 * Uses the new CachingTenantInfoResolver which delegates to
	 * DefaultTenantInfoResolver using the template pattern.
	 *
	 * @param defaultResolver the default resolver to delegate to
	 * @return caching wrapper around default resolver
	 */
	@Bean
	@Primary
	@RequestScope
	public TenantInfoResolver cachingTenantInfoResolver(
		DefaultTenantInfoResolver defaultResolver) {

		return new CachingTenantInfoResolver(defaultResolver);
	}

	/**
	 * Primary user info resolver with request-scoped caching.
	 * Uses the new CachingUserInfoResolver which delegates to
	 * DefaultUserInfoResolver using the template pattern.
	 *
	 * @param defaultResolver the default resolver to delegate to
	 * @return caching wrapper around default resolver
	 */
	@Bean
	@Primary
	@RequestScope
	public UserInfoResolver cachingUserInfoResolver(
		DefaultUserInfoResolver defaultResolver) {

		return new CachingUserInfoResolver(defaultResolver);
	}

}