/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.service.impl;

import com.easybase.context.api.util.PermissionChecker;
import com.easybase.core.auth.action.PermissionActions;
import com.easybase.core.auth.domain.entity.ResourceAction;
import com.easybase.core.auth.service.ResourceActionLocalService;
import com.easybase.core.auth.service.ResourceActionService;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ResourceActionService}.
 * ALWAYS performs permission checks before delegating to ResourceActionLocalService.
 * Never performs persistence directly - always delegates to ResourceActionLocalService.
 *
 * <p>If permission checks are not needed, use ResourceActionLocalService directly.</p>
 *
 * @author Akhash R
 */
@RequiredArgsConstructor
@Service
public class ResourceActionServiceImpl implements ResourceActionService {

	@Override
	public void activateResourceAction(UUID actionId) {
		_permissionChecker.check(PermissionActions.UPDATE);

		_resourceActionLocalService.activateResourceAction(actionId);
	}

	@Override
	public ResourceAction createResourceAction(
		String resourceType, String actionKey, String actionName, int bitValue,
		String description) {

		_permissionChecker.check(PermissionActions.CREATE);

		return _resourceActionLocalService.createResourceAction(
			resourceType, actionKey, actionName, bitValue, description);
	}

	@Override
	public void deactivateResourceAction(UUID actionId) {
		_permissionChecker.check(PermissionActions.UPDATE);

		_resourceActionLocalService.deactivateResourceAction(actionId);
	}

	@Override
	public void deleteResourceAction(UUID actionId) {
		_permissionChecker.check(PermissionActions.DELETE);

		_resourceActionLocalService.deleteResourceAction(actionId);
	}

	@Override
	public List<ResourceAction> getActiveResourceActions(String resourceType) {
		_permissionChecker.check(PermissionActions.VIEW);

		return _resourceActionLocalService.getActiveResourceActions(
			resourceType);
	}

	@Override
	public List<ResourceAction> getAllActiveResourceActions() {
		_permissionChecker.check(PermissionActions.LIST);

		return _resourceActionLocalService.getAllActiveResourceActions();
	}

	@Override
	public ResourceAction getResourceAction(
		String resourceType, String actionKey) {

		_permissionChecker.check(PermissionActions.VIEW);

		return _resourceActionLocalService.getResourceAction(
			resourceType, actionKey);
	}

	@Override
	public ResourceAction getResourceActionById(UUID actionId) {
		_permissionChecker.check(PermissionActions.VIEW);

		return _resourceActionLocalService.getResourceActionById(actionId);
	}

	@Override
	public List<ResourceAction> getResourceActions(String resourceType) {
		_permissionChecker.check(PermissionActions.VIEW);

		return _resourceActionLocalService.getResourceActions(resourceType);
	}

	@Override
	public List<ResourceAction> getResourceActionsOrdered(String resourceType) {
		_permissionChecker.check(PermissionActions.VIEW);

		return _resourceActionLocalService.getResourceActionsOrdered(
			resourceType);
	}

	@Override
	public boolean resourceActionExists(String resourceType, String actionKey) {
		_permissionChecker.check(PermissionActions.VIEW);

		return _resourceActionLocalService.resourceActionExists(
			resourceType, actionKey);
	}

	@Override
	public ResourceAction updateResourceAction(
		UUID actionId, String actionName, String description, boolean active) {

		_permissionChecker.check(PermissionActions.UPDATE);

		return _resourceActionLocalService.updateResourceAction(
			actionId, actionName, description, active);
	}

	private final PermissionChecker _permissionChecker;
	private final ResourceActionLocalService _resourceActionLocalService;

}