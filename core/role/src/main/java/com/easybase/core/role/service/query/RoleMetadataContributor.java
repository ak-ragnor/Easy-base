/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.role.service.query;

import com.easybase.core.role.action.RoleActions;
import com.easybase.core.search.metadata.AbstractSingleKeyMetadataContributor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class RoleMetadataContributor
	extends AbstractSingleKeyMetadataContributor {

	@Override
	public String getEntityType() {
		return "role";
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
	protected Map<String, Class<?>> entityFieldTypes() {
		return _entityFieldTypes;
	}

	@Override
	protected Set<String> entityFilterableFields() {
		return _entityFilterableFields;
	}

	@Override
	protected Set<String> entitySortableFields() {
		return _entitySortableFields;
	}

	private static final Map<String, Class<?>> _entityFieldTypes = Map.of(
		"active", Boolean.class, "description", String.class, "name",
		String.class, "system", Boolean.class, "tenantId", UUID.class);
	private static final Set<String> _entityFilterableFields = Set.of(
		"active", "description", "name", "system", "tenantId");
	private static final Set<String> _entitySortableFields = Set.of(
		"active", "name", "system");
	private static final Set<String> _searchableFields = Set.of(
		"description", "name");

}