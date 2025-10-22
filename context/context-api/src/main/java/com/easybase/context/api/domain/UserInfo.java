/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.domain;

import com.easybase.context.api.constants.ServiceContextConstants;
import com.easybase.context.api.util.LazyValue;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Immutable user information DTO with lazy loading capabilities.
 * Expensive fields like displayName and roles are resolved on-demand
 * using LazyValue for thread-safe lazy loading.
 *
 * @author Akhash R
 */
public final class UserInfo {

	/**
	 * Creates an anonymous user instance.
	 *
	 * @return anonymous user info
	 */
	public static UserInfo anonymous() {
		return new UserInfo(
			ServiceContextConstants.ANONYMOUS_ID, null, false,
			() -> "Anonymous User", List::of, List::of);
	}

	public UserInfo(
		UUID id, String email, boolean active,
		Supplier<String> displayNameSupplier,
		Supplier<List<String>> rolesSupplier,
		Supplier<List<String>> scopesSupplier) {

		_id = id;
		_email = email;
		_active = active;

		_displayName = new LazyValue<>(displayNameSupplier);
		_roles = new LazyValue<>(rolesSupplier);
		_scopes = new LazyValue<>(scopesSupplier);
	}

	/**
	 * Gets the user's active status.
	 *
	 * @return true if user is active
	 */
	public boolean active() {
		return _active;
	}

	/**
	 * Gets the user's display name (lazily resolved).
	 *
	 * @return the display name
	 */
	public String displayName() {
		return _displayName.get();
	}

	/**
	 * Gets the user's email address.
	 *
	 * @return the email address, may be null for system users
	 */
	public String email() {
		return _email;
	}

	/**
	 * Gets the user's unique identifier.
	 *
	 * @return the user ID
	 */
	public UUID id() {
		return _id;
	}

	/**
	 * Gets the user's roles (lazily resolved).
	 *
	 * @return list of role names
	 */
	public List<String> roles() {
		return _roles.get();
	}

	/**
	 * Gets the user's scopes/permissions (lazily resolved).
	 *
	 * @return list of scope names
	 */
	public List<String> scopes() {
		return _scopes.get();
	}

	private final boolean _active;
	private final LazyValue<String> _displayName;
	private final String _email;
	private final UUID _id;
	private final LazyValue<List<String>> _roles;
	private final LazyValue<List<String>> _scopes;

}