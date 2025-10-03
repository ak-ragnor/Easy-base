/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service;

import com.easybase.common.exception.ConflictException;
import com.easybase.common.exception.ResourceNotFoundException;
import com.easybase.core.auth.entity.ResourceAction;
import com.easybase.core.auth.repository.ResourceActionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing resource actions.
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
@Transactional
public class ResourceActionService {

	/**
	 * Activate a resource action.
	 *
	 * @param actionId the action ID
	 */
	public void activateResourceAction(UUID actionId) {
		ResourceAction action = getResourceActionById(actionId);

		action.setActive(true);
		_resourceActionRepository.save(action);
	}

	/**
	 * Create a new resource action.
	 *
	 * @param resourceType the resource type
	 * @param actionKey the action key
	 * @param actionName the action name
	 * @param bitValue the bit value
	 * @param description the description
	 * @return the created resource action
	 */
	public ResourceAction createResourceAction(
		String resourceType, String actionKey, String actionName, int bitValue,
		String description) {

		if (_resourceActionRepository.existsByResourceTypeAndActionKey(
				resourceType, actionKey)) {

			throw new ConflictException(
				"Resource action already exists: " + resourceType + ":" +
					actionKey);
		}

		ResourceAction action = new ResourceAction();

		action.setResourceType(resourceType);
		action.setActionKey(actionKey);
		action.setActionName(actionName);
		action.setBitValue(bitValue);
		action.setDescription(description);
		action.setActive(true);

		return _resourceActionRepository.save(action);
	}

	/**
	 * Deactivate a resource action.
	 *
	 * @param actionId the action ID
	 */
	public void deactivateResourceAction(UUID actionId) {
		ResourceAction action = getResourceActionById(actionId);

		action.setActive(false);
		_resourceActionRepository.save(action);
	}

	/**
	 * Delete a resource action.
	 * WARNING: This should be used with caution as it may break existing permissions.
	 *
	 * @param actionId the action ID
	 */
	public void deleteResourceAction(UUID actionId) {
		ResourceAction action = getResourceActionById(actionId);

		_resourceActionRepository.delete(action);
	}

	/**
	 * Get all active resource actions for a resource type.
	 *
	 * @param resourceType the resource type
	 * @return list of active resource actions
	 */
	@Transactional(readOnly = true)
	public List<ResourceAction> getActiveResourceActions(String resourceType) {
		return _resourceActionRepository.findByResourceTypeAndActiveTrue(
			resourceType);
	}

	/**
	 * Get all active resource actions.
	 *
	 * @return list of all active resource actions
	 */
	@Transactional(readOnly = true)
	public List<ResourceAction> getAllActiveResourceActions() {
		return _resourceActionRepository.findByActiveTrue();
	}

	/**
	 * Get a resource action by resource type and action key.
	 *
	 * @param resourceType the resource type
	 * @param actionKey the action key
	 * @return the resource action
	 */
	@Transactional(readOnly = true)
	public ResourceAction getResourceAction(
		String resourceType, String actionKey) {

		Optional<ResourceAction> actionOptional =
			_resourceActionRepository.findByResourceTypeAndActionKey(
				resourceType, actionKey);

		return actionOptional.orElseThrow(
			() -> new ResourceNotFoundException(
				"Resource action not found: " + resourceType + ":" +
					actionKey));
	}

	/**
	 * Get a resource action by ID.
	 *
	 * @param actionId the action ID
	 * @return the resource action
	 */
	@Transactional(readOnly = true)
	public ResourceAction getResourceActionById(UUID actionId) {
		Optional<ResourceAction> actionOptional =
			_resourceActionRepository.findById(actionId);

		return actionOptional.orElseThrow(
			() -> new ResourceNotFoundException(
				"Resource action not found with id: " + actionId));
	}

	/**
	 * Get all resource actions for a resource type.
	 *
	 * @param resourceType the resource type
	 * @return list of resource actions
	 */
	@Transactional(readOnly = true)
	public List<ResourceAction> getResourceActions(String resourceType) {
		return _resourceActionRepository.findByResourceType(resourceType);
	}

	/**
	 * Get all resource actions sorted by bit value.
	 *
	 * @param resourceType the resource type
	 * @return list of resource actions ordered by bit value
	 */
	@Transactional(readOnly = true)
	public List<ResourceAction> getResourceActionsOrdered(String resourceType) {
		return _resourceActionRepository.findByResourceTypeOrderByBitValue(
			resourceType);
	}

	/**
	 * Check if a resource action exists.
	 *
	 * @param resourceType the resource type
	 * @param actionKey the action key
	 * @return true if the action exists
	 */
	@Transactional(readOnly = true)
	public boolean resourceActionExists(String resourceType, String actionKey) {
		return _resourceActionRepository.existsByResourceTypeAndActionKey(
			resourceType, actionKey);
	}

	/**
	 * Update a resource action.
	 *
	 * @param actionId the action ID
	 * @param actionName the new action name
	 * @param description the new description
	 * @param active the active status
	 * @return the updated resource action
	 */
	public ResourceAction updateResourceAction(
		UUID actionId, String actionName, String description, boolean active) {

		ResourceAction action = getResourceActionById(actionId);

		if (actionName != null) {
			action.setActionName(actionName);
		}

		if (description != null) {
			action.setDescription(description);
		}

		action.setActive(active);

		return _resourceActionRepository.save(action);
	}

	private final ResourceActionRepository _resourceActionRepository;

}