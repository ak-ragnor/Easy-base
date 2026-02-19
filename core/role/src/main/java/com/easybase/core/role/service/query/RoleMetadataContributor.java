/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.service.query;

import com.easybase.core.role.action.RoleActions;
import com.easybase.core.search.metadata.QueryMetadataContributor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class RoleMetadataContributor implements QueryMetadataContributor {

	@Override
	public String getEntityType() {
		return "role";
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
		return RoleActions.LIST;
	}

	@Override
	public Set<String> getSearchableFields() {
		return _searchableFields;
	}

	@Override
	public Set<String> getSortableFields() {
		return _sortableFields;
	}

	private static final Map<String, Class<?>> _fieldTypes = Map.of(
		"active", Boolean.class, "description", String.class, "id", UUID.class,
		"name", String.class, "system", Boolean.class, "tenantId", UUID.class);
	private static final Set<String> _filterableFields = Set.of(
		"active", "description", "id", "name", "system", "tenantId");
	private static final Set<String> _searchableFields = Set.of(
		"description", "name");
	private static final Set<String> _sortableFields = Set.of(
		"active", "id", "name", "system");

}