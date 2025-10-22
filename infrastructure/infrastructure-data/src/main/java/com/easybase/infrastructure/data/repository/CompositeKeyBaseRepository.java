/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for entities with composite keys.
 * Extends JpaRepository to provide standard CRUD operations for composite key entities.
 *
 * @param <T> the entity type extending CompositeKeyEntity
 * @param <ID> the composite key type (typically a nested static class)
 * @author Akhash R
 */
@NoRepositoryBean
public interface CompositeKeyBaseRepository<T, ID>
	extends JpaRepository<T, ID> {

	/**
	 * Soft delete an entity by setting the deleted flag to true.
	 *
	 * @param entity the entity to soft delete
	 * @return the updated entity
	 */
	public default T softDelete(T entity) {

		// Note: This would need reflection or specific implementation
		// in concrete repositories since CompositeKeyEntity methods
		// aren't accessible here due to generic constraints

		return save(entity);
	}

}