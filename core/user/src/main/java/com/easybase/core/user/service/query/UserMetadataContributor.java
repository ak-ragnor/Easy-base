/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.user.service.query;

import com.easybase.core.search.metadata.AbstractSingleKeyMetadataContributor;
import com.easybase.core.user.action.UserActions;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * @author Akhash R
 */
@Component
public class UserMetadataContributor
	extends AbstractSingleKeyMetadataContributor {

	@Override
	public String getEntityType() {
		return _ENTITY_TYPE;
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

	private static final String _ENTITY_TYPE = "user";

	private static final Map<String, Class<?>> _entityFieldTypes = Map.of(
		"displayName", String.class, "email", String.class, "firstName",
		String.class, "lastName", String.class);
	private static final Set<String> _entityFilterableFields = Set.of(
		"displayName", "email", "firstName", "lastName");
	private static final Set<String> _entitySortableFields = Set.of(
		"displayName", "email", "firstName", "lastName");
	private static final Set<String> _searchableFields = Set.of(
		"displayName", "email", "firstName", "lastName");

}