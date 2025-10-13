/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service;

import com.easybase.core.auth.entity.ResourceAction;

import java.util.List;
import java.util.UUID;

/**
 * Local service interface for resource action business logic.
 * Contains all business logic, repository calls, and transaction management.
 * Does NOT perform permission checks - that's the responsibility of ResourceActionService.
 *
 * @author Akhash R
 */
public interface ResourceActionLocalService {

	/**
	 * Activate a resource action.
	 *
	 * @param actionId the action ID
	 */
	void activateResourceAction(UUID actionId);

	/**
	 * Create a new resource action.
	 *
	 * @param resourceType the resource type
	 * @param actionKey the action key
	 * @param actionName the action name
	 * @param bitValue the bit value
	 * @param description the description
	 * @return the created resource action
	 * @throws com.easybase.common.exception.ConflictException if action already exists
	 */
	ResourceAction createResourceAction(
		String resourceType, String actionKey, String actionName, int bitValue,
		String description);

	/**
	 * Deactivate a resource action.
	 *
	 * @param actionId the action ID
	 */
	void deactivateResourceAction(UUID actionId);

	/**
	 * Delete a resource action.
	 * WARNING: This should be used with caution as it may break existing permissions.
	 *
	 * @param actionId the action ID
	 */
	void deleteResourceAction(UUID actionId);

	/**
	 * Get all active resource actions for a resource type.
	 *
	 * @param resourceType the resource type
	 * @return list of active resource actions
	 */
	List<ResourceAction> getActiveResourceActions(String resourceType);

	/**
	 * Get all active resource actions.
	 *
	 * @return list of all active resource actions
	 */
	List<ResourceAction> getAllActiveResourceActions();

	/**
	 * Get a resource action by resource type and action key.
	 *
	 * @param resourceType the resource type
	 * @param actionKey the action key
	 * @return the resource action
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	ResourceAction getResourceAction(String resourceType, String actionKey);

	/**
	 * Get a resource action by ID.
	 *
	 * @param actionId the action ID
	 * @return the resource action
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	ResourceAction getResourceActionById(UUID actionId);

	/**
	 * Get all resource actions for a resource type.
	 *
	 * @param resourceType the resource type
	 * @return list of resource actions
	 */
	List<ResourceAction> getResourceActions(String resourceType);

	/**
	 * Get all resource actions sorted by bit value.
	 *
	 * @param resourceType the resource type
	 * @return list of resource actions ordered by bit value
	 */
	List<ResourceAction> getResourceActionsOrdered(String resourceType);

	/**
	 * Check if a resource action exists.
	 *
	 * @param resourceType the resource type
	 * @param actionKey the action key
	 * @return true if the action exists
	 */
	boolean resourceActionExists(String resourceType, String actionKey);

	/**
	 * Update a resource action.
	 *
	 * @param actionId the action ID
	 * @param actionName the new action name
	 * @param description the new description
	 * @param active the active status
	 * @return the updated resource action
	 * @throws com.easybase.common.exception.ResourceNotFoundException if not found
	 */
	ResourceAction updateResourceAction(
		UUID actionId, String actionName, String description, boolean active);

}