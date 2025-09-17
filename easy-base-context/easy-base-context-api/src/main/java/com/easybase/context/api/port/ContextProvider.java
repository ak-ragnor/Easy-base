/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.port;

import com.easybase.context.api.domain.ServiceContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * SPI interface for building ServiceContext from HTTP requests.
 * Implementations can provide different context resolution strategies
 * based on authentication method, environment, or testing needs.
 *
 * @author Akhash R
 */
public interface ContextProvider {

	/**
	 * Builds a ServiceContext from the given HTTP request.
	 *
	 * @param request the HTTP request to process
	 * @return a fully constructed ServiceContext
	 */
	public ServiceContext build(HttpServletRequest request);

}