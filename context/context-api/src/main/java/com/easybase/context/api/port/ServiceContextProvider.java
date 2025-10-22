/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.port;

import com.easybase.context.api.domain.ServiceContext;

/**
 * SPI interface for building ServiceContext.
 * Implementations can provide different context resolution strategies
 * based on authentication method, environment, or testing needs.
 *
 * @author Akhash R
 */
public interface ServiceContextProvider {

	/**
	 * Builds a ServiceContext from principal data.
	 *
	 * @param principal the principal data (implementation specific)
	 * @return a fully constructed ServiceContext
	 */
	public ServiceContext build(Object principal);

}