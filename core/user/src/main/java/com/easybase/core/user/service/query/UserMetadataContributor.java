/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.service.query;

import com.easybase.core.search.metadata.QueryMetadataContributor;
import com.easybase.core.user.action.UserActions;

import java.time.LocalDateTime;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class UserMetadataContributor implements QueryMetadataContributor {

	@Override
	public String getEntityType() {
		return _ENTITY_TYPE;
	}

	@Override
	public Map<String, Class<?>> getFieldTypes() {
		return _fieldTypes;
	}

	@Override
	public Set<String> getFilterableFields() {
		return _filterableFields;
	}

	@Override
	public String getRequiredPermission() {
		return UserActions.LIST;
	}

	@Override
	public Set<String> getSearchableFields() {
		return _searchableFields;
	}

	@Override
	public Set<String> getSortableFields() {
		return _sortableFields;
	}

	private static final String _ENTITY_TYPE = "user";

	private static final Map<String, Class<?>> _fieldTypes = Map.of(
		"createdAt", LocalDateTime.class, "displayName", String.class, "email",
		String.class, "firstName", String.class, "id", UUID.class, "lastName",
		String.class, "updatedAt", LocalDateTime.class);
	private static final Set<String> _filterableFields = Set.of(
		"createdAt", "displayName", "email", "firstName", "id", "lastName",
		"updatedAt");
	private static final Set<String> _searchableFields = Set.of(
		"displayName", "email", "firstName", "lastName");
	private static final Set<String> _sortableFields = Set.of(
		"createdAt", "displayName", "email", "firstName", "id", "lastName",
		"updatedAt");

}