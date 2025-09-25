/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.action;

/**
 * Action constants for permission-related operations.
 *
 * @author Akhash R
 */
public final class PermissionActions {

	public static final String CREATE = "PERMISSION:CREATE";

	public static final String DELETE = "PERMISSION:DELETE";

	public static final String LIST = "PERMISSION:LIST";

	public static final String UPDATE = "PERMISSION:UPDATE";

	public static final String VIEW = "PERMISSION:VIEW";

	private PermissionActions() {
		throw new UnsupportedOperationException("Constants class");
	}

}