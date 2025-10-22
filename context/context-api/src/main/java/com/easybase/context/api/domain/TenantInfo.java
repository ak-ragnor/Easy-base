/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.domain;

import com.easybase.context.api.constants.ServiceContextConstants;
import com.easybase.context.api.util.LazyValue;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Immutable tenant information DTO with lazy loading capabilities.
 * Expensive fields like name and settings are resolved on-demand
 * using LazyValue for thread-safe lazy loading.
 *
 * @author Akhash R
 */
public final class TenantInfo {

	/**
	 * Creates a public tenant instance for anonymous access.
	 *
	 * @return public tenant info
	 */
	public static TenantInfo publicTenant() {
		return new TenantInfo(
			ServiceContextConstants.PUBLIC_TENANT_ID, true, () -> "Public",
			Map::of);
	}

	public TenantInfo(
		UUID id, boolean active, Supplier<String> nameSupplier,
		Supplier<Map<String, String>> settingsSupplier) {

		_id = id;
		_active = active;

		_name = new LazyValue<>(nameSupplier);
		_settings = new LazyValue<>(settingsSupplier);
	}

	/**
	 * Gets the tenant's active status.
	 *
	 * @return true if tenant is active
	 */
	public boolean active() {
		return _active;
	}

	/**
	 * Gets the tenant's unique identifier.
	 *
	 * @return the tenant ID
	 */
	public UUID id() {
		return _id;
	}

	/**
	 * Gets the tenant's name (lazily resolved).
	 *
	 * @return the tenant name
	 */
	public String name() {
		return _name.get();
	}

	/**
	 * Gets the tenant's settings (lazily resolved).
	 *
	 * @return map of setting key-value pairs
	 */
	public Map<String, String> settings() {
		return _settings.get();
	}

	private final boolean _active;
	private final UUID _id;
	private final LazyValue<String> _name;
	private final LazyValue<Map<String, String>> _settings;

}