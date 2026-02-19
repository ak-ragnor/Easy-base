/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.search;

import java.util.List;

/**
 * Paginated query result wrapper.
 *
 * @param <T> the result entity type
 * @author Akhash R
 */
public class QueryResult<T> {

	public QueryResult(
		List<T> content, int page, int size, long totalElements) {

		_content = content;
		_page = page;
		_size = size;
		_totalElements = totalElements;
	}

	public List<T> getContent() {
		return _content;
	}

	public int getPage() {
		return _page;
	}

	public int getSize() {
		return _size;
	}

	public long getTotalElements() {
		return _totalElements;
	}

	private final List<T> _content;
	private final int _page;
	private final int _size;
	private final long _totalElements;

}