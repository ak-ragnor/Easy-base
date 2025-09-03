/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.api.context;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.repository.CrudRepository;

/**
 * Abstract template for default resolver implementations that resolve
 * information from database entities. Uses Template Method pattern to
 * provide common resolution logic while allowing subclasses to customize
 * specific behaviors.
 *
 * @param <K> the key type (typically UUID)
 * @param <V> the value type being resolved (UserInfo, TenantInfo, etc.)
 * @param <E> the entity type from database
 * @author Akhash R
 */
@Slf4j
public abstract class AbstractDefaultResolver<K, V, E> {

	/**
	 * Resolves information for the given key using the template method pattern.
	 *
	 * @param key the key to resolve (typically UUID)
	 * @return the resolved information object
	 * @throws IllegalArgumentException if key is null or entity not found
	 */
	public final V resolve(K key) {
		validateKey(key);

		if (isAnonymousKey(key)) {
			return createAnonymousInstance();
		}

		Optional<E> entityOptional = getRepository().findById(key);

		if (entityOptional.isPresent()) {
			E entity = entityOptional.get();

			logEntityResolution(entity);

			return mapEntityToInfo(entity);
		}

		throw new IllegalArgumentException(
			getEntityType() + " not found: " + key);
	}

	/**
	 * Creates an anonymous instance for cases where no specific entity exists.
	 * Subclasses provide their anonymous/default implementation.
	 *
	 * @return an anonymous instance of the info type
	 */
	protected abstract V createAnonymousInstance();

	/**
	 * Gets the entity type name for error messages.
	 * Subclasses provide their entity name (e.g., "User", "Tenant").
	 *
	 * @return the entity type name
	 */
	protected abstract String getEntityType();

	/**
	 * Gets the repository for database access.
	 * Subclasses must provide their specific repository implementation.
	 *
	 * @return the repository for entity access
	 */
	protected abstract CrudRepository<E, K> getRepository();

	/**
	 * Checks if the given key represents an anonymous entity.
	 * Default implementation checks for the standard anonymous UUID.
	 * Subclasses can override for custom anonymous key logic.
	 *
	 * @param key the key to check
	 * @return true if key represents anonymous access
	 */
	protected boolean isAnonymousKey(K key) {
		return ServiceContextConstants.ANONYMOUS_ID.equals(key);
	}

	/**
	 * Checks if an entity is active (not deleted).
	 * Default implementation checks for Boolean.TRUE.equals(entity.getDeleted()).
	 * Subclasses can override this if they have different active/deleted logic.
	 *
	 * @param entity the entity to check
	 * @return true if entity is active
	 */
	protected boolean isEntityActive(E entity) {
		try {

			// Use reflection to check for getDeleted method

			var method = entity.getClass(
			).getMethod(
				"getDeleted"
			);
			Boolean deleted = (Boolean)method.invoke(entity);

			return !Boolean.TRUE.equals(deleted);
		}
		catch (Exception e) {

			// If no getDeleted method or reflection fails, assume active

			log.debug(
				"Could not determine deleted status for entity: {}", entity);

			return true;
		}
	}

	/**
	 * Logs entity resolution for debugging purposes.
	 * Default implementation logs at info level.
	 * Subclasses can override for different logging behavior.
	 *
	 * @param entity the resolved entity
	 */
	protected void logEntityResolution(E entity) {
		log.debug("Resolved {} entity: {}", getEntityType(), entity);
	}

	/**
	 * Maps a database entity to the corresponding info object.
	 * Subclasses implement the specific mapping logic.
	 *
	 * @param entity the database entity
	 * @return the mapped info object
	 */
	protected abstract V mapEntityToInfo(E entity);

	/**
	 * Validates the input key.
	 * Default implementation checks for null.
	 * Subclasses can override for additional validation.
	 *
	 * @param key the key to validate
	 * @throws IllegalArgumentException if key is invalid
	 */
	protected void validateKey(K key) {
		if (key == null) {
			throw new IllegalArgumentException(
				getEntityType() + " ID cannot be null");
		}
	}

}