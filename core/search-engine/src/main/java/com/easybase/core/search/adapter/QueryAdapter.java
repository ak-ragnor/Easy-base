/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.search.adapter;

import com.easybase.infrastructure.search.QueryResult;
import com.easybase.infrastructure.search.SearchContext;

/**
 * Adapter interface for executing queries against a specific entity type.
 *
 * @param <T> the entity type
 * @author Akhash R
 */
public interface QueryAdapter<T> {

	/**
	 * Execute a query using the given search context.
	 *
	 * @param context the search context
	 * @return paginated query result
	 */
	public QueryResult<T> execute(SearchContext context);

	/**
	 * Return the entity type identifier this adapter handles (e.g.
	 * {@code "user"}).
	 *
	 * @return the entity type string
	 */
	public String getEntityType();

}