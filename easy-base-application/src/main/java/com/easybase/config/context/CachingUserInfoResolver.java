/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.config.context;

import com.easybase.common.api.context.CachingResolver;
import com.easybase.common.api.context.UserInfo;
import com.easybase.common.api.context.UserInfoResolver;

import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Request-scoped caching wrapper for UserInfoResolver.
 * Uses the generic CachingResolver to eliminate code duplication
 * and provide consistent caching behavior.
 *
 * @author Akhash R
 */
@Component
@Primary
@RequestScope
public class CachingUserInfoResolver implements UserInfoResolver {

	/**
	 * Constructor that creates the caching resolver with the delegate.
	 *
	 * @param delegate the underlying resolver to delegate cache misses to
	 */
	public CachingUserInfoResolver(UserInfoResolver delegate) {
		_cachingResolver = new CachingResolver<>(delegate::resolve);
	}

	@Override
	public UserInfo resolve(UUID userId) {
		return _cachingResolver.apply(userId);
	}

	private final CachingResolver<UUID, UserInfo> _cachingResolver;

}