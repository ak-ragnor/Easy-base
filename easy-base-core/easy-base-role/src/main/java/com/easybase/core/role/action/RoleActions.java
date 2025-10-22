/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.action;

import com.easybase.infrastructure.auth.annotation.ActionDefinition;
import com.easybase.infrastructure.auth.annotation.ActionRoles;

/**
 * Action constants for role-related permissions.
 *
 * @author Akhash R
 */
@ActionDefinition(resourceType = "ROLE")
public final class RoleActions {

	public static final String ADD_PERMISSION = "ROLE:ADD_PERMISSION";

	public static final String ASSIGN = "ROLE:ASSIGN";

	public static final String CREATE = "ROLE:CREATE";

	public static final String DELETE = "ROLE:DELETE";

	public static final String LIST = "ROLE:LIST";

	public static final String REMOVE_PERMISSION = "ROLE:REMOVE_PERMISSION";

	public static final String REVOKE = "ROLE:REVOKE";

	public static final String UPDATE = "ROLE:UPDATE";

	@ActionRoles("USER")
	public static final String VIEW = "ROLE:VIEW";

	private RoleActions() {
		throw new UnsupportedOperationException("Constants class");
	}

}