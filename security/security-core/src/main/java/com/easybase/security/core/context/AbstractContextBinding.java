/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.core.context;

import com.easybase.security.api.dto.AuthenticatedPrincipalData;

/**
 * Abstract base class for context binding implementations that manage
 * authenticated principal data in thread-local storage.
 *
 * <p>This class provides common functionality for binding, clearing, and
 * retrieving principal data from thread-local storage, eliminating code
 * duplication across different context binding implementations.</p>
 *
 * @author Akhash R
 */
public abstract class AbstractContextBinding {

	/**
	 * Binds the authenticated principal to the current thread.
	 *
	 * @param principal the authenticated principal data to bind
	 */
	public void bind(AuthenticatedPrincipalData principal) {
		_principalHolder.set(principal);
	}

	/**
	 * Clears the authenticated principal from the current thread.
	 */
	public void clear() {
		_principalHolder.remove();
	}

	/**
	 * Gets the authenticated principal from the current thread context.
	 *
	 * @return the authenticated principal data, or null if none is bound
	 */
	public AuthenticatedPrincipalData fromCurrentContext() {
		return _principalHolder.get();
	}

	private final ThreadLocal<AuthenticatedPrincipalData> _principalHolder =
		new ThreadLocal<>();

}