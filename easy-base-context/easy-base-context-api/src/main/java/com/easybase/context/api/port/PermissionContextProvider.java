/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.port;

import com.easybase.context.api.domain.PermissionContext;

/**
 * SPI interface for providing access to the current PermissionContext.
 * This is a simpler interface than PermissionContextBinding that allows
 * modules to access permission context without circular dependencies.
 *
 * @author Akhash R
 */
public interface PermissionContextProvider {

	/**
	 * Gets the current PermissionContext for the thread.
	 *
	 * @return the current PermissionContext, or null if none is available
	 */
	PermissionContext getCurrentPermissionContext();

}