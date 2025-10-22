/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.tenant.action;

/**
 * Action constants for tenant-related permissions.
 *
 * @author Akhash R
 */
public final class TenantActions {

	public static final String ACTIVATE = "TENANT:ACTIVATE";

	public static final String CREATE = "TENANT:CREATE";

	public static final String DEACTIVATE = "TENANT:DEACTIVATE";

	public static final String DELETE = "TENANT:DELETE";

	public static final String LIST = "TENANT:LIST";

	public static final String MANAGE_SETTINGS = "TENANT:MANAGE_SETTINGS";

	public static final String MANAGE_USERS = "TENANT:MANAGE_USERS";

	public static final String UPDATE = "TENANT:UPDATE";

	public static final String VIEW = "TENANT:VIEW";

	private TenantActions() {
		throw new UnsupportedOperationException("Constants class");
	}

}