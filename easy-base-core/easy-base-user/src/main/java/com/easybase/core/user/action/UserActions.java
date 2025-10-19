/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.action;

import com.easybase.infrastructure.auth.annotation.ActionDefinition;

/**
 * Action constants for user-related permissions.
 *
 * @author Akhash R
 */
@ActionDefinition(resourceType = "USER")
public final class UserActions {

	public static final String ACTIVATE = "USER:ACTIVATE";

	public static final String CHANGE_PASSWORD = "USER:CHANGE_PASSWORD";

	public static final String CREATE = "USER:CREATE";

	public static final String DEACTIVATE = "USER:DEACTIVATE";

	public static final String DELETE = "USER:DELETE";

	public static final String LIST = "USER:LIST";

	public static final String RESET_PASSWORD = "USER:RESET_PASSWORD";

	public static final String UPDATE = "USER:UPDATE";

	public static final String VIEW = "USER:VIEW";

	private UserActions() {
		throw new UnsupportedOperationException("Constants class");
	}

}