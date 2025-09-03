/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.api.context;

import java.util.UUID;

/**
 * SPI interface for resolving user information from a user ID.
 * Implementations can provide different resolution strategies such as
 * database lookup, caching, or testing mocks.
 *
 * @author Akhash R
 */
public interface UserInfoResolver {

	/**
	 * Resolves full user information for the given user ID.
	 *
	 * @param userId the user's unique identifier
	 * @return the resolved user information
	 * @throws IllegalArgumentException if userId is null or invalid
	 */
	public UserInfo resolve(UUID userId);

}