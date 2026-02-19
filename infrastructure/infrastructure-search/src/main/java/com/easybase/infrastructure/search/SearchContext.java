/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.search;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Builder;

/**
 * Mutable context object passed through the query pipeline.
 *
 * @author Akhash R
 */
@Builder
public class SearchContext {

	public static final int DEFAULT_SIZE = 20;

	public static final int MAX_SIZE = 100;

	public <T> T getAttribute(String key) {
		return (T)attributes.get(key);
	}

	public String getEntityType() {
		return entityType;
	}

	public String getFilter() {
		return filter;
	}

	public int getPage() {
		return page;
	}

	public String getSearch() {
		return search;
	}

	public int getSize() {
		if (size > MAX_SIZE) {
			return MAX_SIZE;
		}

		if (size < 1) {
			return DEFAULT_SIZE;
		}

		return size;
	}

	public String getSort() {
		return sort;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public boolean isCheckPermission() {
		return checkPermission;
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	@Builder.Default
	private final Map<String, Object> attributes = new HashMap<>();

	@Builder.Default
	private final boolean checkPermission = true;

	private final String entityType;
	private final String filter;

	@Builder.Default
	private final int page = 0;

	private final String search;

	@Builder.Default
	private final int size = DEFAULT_SIZE;

	private final String sort;
	private final UUID tenantId;

}