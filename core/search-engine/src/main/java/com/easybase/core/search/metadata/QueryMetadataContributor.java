/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.search.metadata;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Contributes field metadata for a specific entity type, enabling
 * pre-validation of filter, sort, and search fields before query
 * execution.
 *
 * @author Akhash R
 */
public interface QueryMetadataContributor {

	/**
	 * Return the entity type identifier this contributor describes (e.g.
	 * {@code "user"}).
	 *
	 * @return the entity type string
	 */
	public String getEntityType();

	/**
	 * Return the mapping of field names to their Java types for type-aware
	 * comparisons. Default returns empty map (string comparison fallback).
	 *
	 * @return field name to type mapping
	 */
	public default Map<String, Class<?>> getFieldTypes() {
		return Collections.emptyMap();
	}

	/**
	 * Return the set of field names that can be used in filter expressions.
	 *
	 * @return filterable field names
	 */
	public Set<String> getFilterableFields();

	/**
	 * Return the permission action required to execute queries on this entity
	 * type, or {@code null} if no permission check is needed.
	 *
	 * @return the required permission action string, or null
	 */
	public default String getRequiredPermission() {
		return null;
	}

	/**
	 * Return the set of field names that support full-text search.
	 *
	 * @return searchable field names
	 */
	public Set<String> getSearchableFields();

	/**
	 * Return the set of field names that can be used in sort expressions.
	 *
	 * @return sortable field names
	 */
	public Set<String> getSortableFields();

}