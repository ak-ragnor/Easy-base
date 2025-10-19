/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.system.action;

/**
 * Action constants for system-wide permissions.
 *
 * @author Akhash R
 */
public final class SystemActions {

	public static final String ADMIN = "SYSTEM:ADMIN";

	public static final String BACKUP = "SYSTEM:BACKUP";

	public static final String MANAGE_CONFIGURATION =
		"SYSTEM:MANAGE_CONFIGURATION";

	public static final String MANAGE_SECURITY = "SYSTEM:MANAGE_SECURITY";

	public static final String RESTORE = "SYSTEM:RESTORE";

	public static final String VIEW_LOGS = "SYSTEM:VIEW_LOGS";

	public static final String VIEW_METRICS = "SYSTEM:VIEW_METRICS";

	private SystemActions() {
		throw new UnsupportedOperationException("Constants class");
	}

}