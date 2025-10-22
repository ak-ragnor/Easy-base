/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.constants;

import java.util.UUID;

/**
 * Constants used throughout the service context system.
 * Centralizes magic values and provides consistent access to common identifiers.
 *
 * @author Akhash R
 */
public final class ServiceContextConstants {

	/**
	 * UUID representing anonymous/unauthenticated users and public tenants.
	 * Uses a nil UUID (all zeros) as a well-known identifier.
	 */
	public static final UUID ANONYMOUS_ID = new UUID(0, 0);

	/**
	 * Alias for ANONYMOUS_ID specifically for tenant context.
	 * Represents the public tenant available to unauthenticated users.
	 */
	public static final UUID PUBLIC_TENANT_ID = ANONYMOUS_ID;

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private ServiceContextConstants() {
		throw new UnsupportedOperationException(
			"ServiceContextConstants is a utility class and cannot be instantiated");
	}

}