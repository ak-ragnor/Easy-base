/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.data.repository;

import com.easybase.infrastructure.data.entity.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * Base repository interface providing common data access patterns for entities
 * that extend BaseEntity. This includes soft delete functionality, active entity
 * queries, and tenant-aware operations.
 *
 * @param <T> the entity type that extends BaseEntity
 * @author Akhash R
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity>
	extends JpaRepository<T, UUID> {

	/**
	 * Checks if an active (non-deleted) entity exists with the given ID.
	 *
	 * @param id the entity ID to check
	 * @return true if an active entity exists with the given ID
	 */
	@Query(
		"SELECT COUNT(e) > 0 FROM #{#entityName} e WHERE e.id = :id AND e.deleted = false"
	)
	public boolean existsActiveById(@Param("id") UUID id);

	/**
	 * Finds an active (non-deleted) entity by ID.
	 *
	 * @param id the entity ID to find
	 * @return optional containing the entity if found and active
	 */
	@Query(
		"SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.deleted = false"
	)
	public Optional<T> findActiveById(@Param("id") UUID id);

	/**
	 * Finds all active (non-deleted) entities.
	 *
	 * @return list of active entities
	 */
	@Query(
		"SELECT e FROM #{#entityName} e WHERE e.deleted = false ORDER BY e.updatedAt DESC"
	)
	public List<T> findAllActive();

	/**
	 * Performs soft delete by setting the deleted flag to true.
	 * This method should be used instead of the standard delete methods
	 * to maintain data integrity and audit trails.
	 *
	 * @param id the ID of the entity to soft delete
	 */
	public default void softDeleteById(UUID id) {
		Optional<T> optionalEntity = findById(id);

		optionalEntity.ifPresent(
			entity -> {
				entity.setDeleted(true);

				save(entity);
			});
	}

}