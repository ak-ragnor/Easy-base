/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.context.api.port;

import com.easybase.context.api.domain.TenantInfo;

import java.util.UUID;

/**
 * SPI interface for resolving tenant information from a tenant ID.
 * Implementations can provide different resolution strategies such as
 * database lookup, caching, or testing mocks.
 *
 * @author Akhash R
 */
public interface TenantInfoResolver {

	/**
	 * Resolves full tenant information for the given tenant ID.
	 *
	 * @param tenantId the tenant's unique identifier
	 * @return the resolved tenant information
	 * @throws IllegalArgumentException if tenantId is null or invalid
	 */
	public TenantInfo resolve(UUID tenantId);

}