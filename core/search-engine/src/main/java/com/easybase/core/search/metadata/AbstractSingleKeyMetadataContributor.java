/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.search.metadata;

import java.time.Instant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract base for entities extending SingleKeyBaseEntity.
 * Automatically includes id, createdAt, updatedAt in field types,
 * filterable fields, and sortable fields.
 *
 * @author Akhash R
 */
public abstract class AbstractSingleKeyMetadataContributor
	implements QueryMetadataContributor {

	@Override
	public final Map<String, Class<?>> getFieldTypes() {
		Map<String, Class<?>> merged = new HashMap<>(_baseFieldTypes);

		merged.putAll(entityFieldTypes());

		return Map.copyOf(merged);
	}

	@Override
	public final Set<String> getFilterableFields() {
		Set<String> merged = new HashSet<>(_baseFilterable);

		merged.addAll(entityFilterableFields());

		return Set.copyOf(merged);
	}

	@Override
	public final Set<String> getSortableFields() {
		Set<String> merged = new HashSet<>(_baseSortable);

		merged.addAll(entitySortableFields());

		return Set.copyOf(merged);
	}

	/**
	 * Entity-specific field types. Subclasses implement this
	 * instead of overriding getFieldTypes() directly.
	 */
	protected abstract Map<String, Class<?>> entityFieldTypes();

	/**
	 * Entity-specific filterable fields (excluding base entity fields).
	 */
	protected abstract Set<String> entityFilterableFields();

	/**
	 * Entity-specific sortable fields (excluding base entity fields).
	 */
	protected abstract Set<String> entitySortableFields();

	private static final Map<String, Class<?>> _baseFieldTypes = Map.of(
		"createdAt", Instant.class, "id", UUID.class, "updatedAt",
		Instant.class);
	private static final Set<String> _baseFilterable = Set.of(
		"createdAt", "id", "updatedAt");
	private static final Set<String> _baseSortable = Set.of(
		"createdAt", "id", "updatedAt");

}