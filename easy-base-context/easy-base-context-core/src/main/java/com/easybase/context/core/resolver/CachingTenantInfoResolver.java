/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.core.resolver;

import com.easybase.context.api.domain.TenantInfo;
import com.easybase.context.api.port.CachingResolver;
import com.easybase.context.api.port.TenantInfoResolver;

import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Request-scoped caching wrapper for TenantInfoResolver.
 * Uses the generic CachingResolver to eliminate code duplication
 * and provide consistent caching behavior.
 *
 * @author Akhash R
 */
@Component
@Primary
@RequestScope
public class CachingTenantInfoResolver implements TenantInfoResolver {

	/**
	 * Constructor that creates the caching resolver with the delegate.
	 *
	 * @param delegate the underlying resolver to delegate cache misses to
	 */
	public CachingTenantInfoResolver(TenantInfoResolver delegate) {
		_cachingResolver = new CachingResolver<>(delegate::resolve);
	}

	@Override
	public TenantInfo resolve(UUID tenantId) {
		return _cachingResolver.apply(tenantId);
	}

	private final CachingResolver<UUID, TenantInfo> _cachingResolver;

}