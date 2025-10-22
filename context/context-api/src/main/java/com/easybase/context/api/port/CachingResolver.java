/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.port;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Generic request-scoped caching wrapper for any resolver type.
 * Provides thread-safe caching to avoid multiple expensive resolution calls
 * within the same request scope.
 *
 * @param <K> the key type for resolution
 * @param <V> the value type being resolved
 * @author Akhash R
 */
public final class CachingResolver<K, V> implements Function<K, V> {

	/**
	 * Creates a new caching resolver with the given delegate.
	 *
	 * @param delegate the underlying resolver to delegate cache misses to
	 * @throws IllegalArgumentException if delegate is null
	 */
	public CachingResolver(Function<K, V> delegate) {
		if (delegate == null) {
			throw new IllegalArgumentException(
				"Delegate resolver cannot be null");
		}

		_delegate = delegate;
	}

	/**
	 * Resolves the value for the given key, using cache if available.
	 *
	 * @param key the key to resolve
	 * @return the resolved value
	 */
	@Override
	public V apply(K key) {
		return _cache.computeIfAbsent(key, _delegate);
	}

	/**
	 * Clears all cached values. Useful for testing or when cache needs refresh.
	 */
	public void clearCache() {
		_cache.clear();
	}

	/**
	 * Gets the current cache size.
	 *
	 * @return number of cached entries
	 */
	public int getCacheSize() {
		return _cache.size();
	}

	/**
	 * Checks if a key is present in the cache.
	 *
	 * @param key the key to check
	 * @return true if key is cached, false otherwise
	 */
	public boolean isCached(K key) {
		return _cache.containsKey(key);
	}

	private final Map<K, V> _cache = new ConcurrentHashMap<>();
	private final Function<K, V> _delegate;

}