/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.constants;

/**
 * Dynamic action keys for permission checking.
 * These constants are used with ResourcePermissionUtil.check().
 *
 * @author Akhash R
 */
public final class ActionKeys {

	// Collection Actions

	public static final String ADD_COLLECTION = "COLLECTION:CREATE";

	public static final String ADD_PERMISSION = "PERMISSION:CREATE";

	public static final String ADD_RECORD = "RECORD:CREATE";

	public static final String ADD_ROLE = "ROLE:CREATE";

	public static final String ADD_TENANT = "TENANT:CREATE";

	// User Actions

	public static final String ADD_USER = "USER:CREATE";

	public static final String ASSIGN_ROLE = "ROLE:ASSIGN";

	public static final String DELETE_COLLECTION = "COLLECTION:DELETE";

	public static final String DELETE_PERMISSION = "PERMISSION:DELETE";

	public static final String DELETE_RECORD = "RECORD:DELETE";

	// Role Actions

	public static final String DELETE_ROLE = "ROLE:DELETE";

	public static final String DELETE_TENANT = "TENANT:DELETE";

	public static final String DELETE_USER = "USER:DELETE";

	public static final String LIST_COLLECTIONS = "COLLECTION:LIST";

	public static final String LIST_PERMISSIONS = "PERMISSION:LIST";

	public static final String LIST_RECORDS = "RECORD:LIST";

	public static final String LIST_ROLES = "ROLE:LIST";

	// Permission Actions

	public static final String LIST_TENANTS = "TENANT:LIST";

	public static final String LIST_USERS = "USER:LIST";

	public static final String REVOKE_ROLE = "ROLE:REVOKE";

	public static final String UPDATE_COLLECTION = "COLLECTION:UPDATE";

	public static final String UPDATE_PERMISSION = "PERMISSION:UPDATE";

	// Data Record Actions

	public static final String UPDATE_RECORD = "RECORD:UPDATE";

	public static final String UPDATE_ROLE = "ROLE:UPDATE";

	public static final String UPDATE_SYSTEM_CONFIG = "SYSTEM:UPDATE";

	public static final String UPDATE_TENANT = "TENANT:UPDATE";

	public static final String UPDATE_USER = "USER:UPDATE";

	// System Actions

	public static final String VIEW_COLLECTION = "COLLECTION:READ";

	public static final String VIEW_PERMISSION = "PERMISSION:READ";

	// Tenant Actions

	public static final String VIEW_RECORD = "RECORD:READ";

	public static final String VIEW_ROLE = "ROLE:READ";

	public static final String VIEW_SYSTEM_INFO = "SYSTEM:READ";

	public static final String VIEW_TENANT = "TENANT:READ";

	public static final String VIEW_USER = "USER:READ";

	private ActionKeys() {
		throw new UnsupportedOperationException("Utility class");
	}

}