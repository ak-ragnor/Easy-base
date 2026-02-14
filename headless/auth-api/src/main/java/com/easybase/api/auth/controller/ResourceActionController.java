/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.api.auth.controller;

import com.easybase.api.auth.dto.ResourceActionDto;
import com.easybase.api.auth.dto.mapper.ResourceActionMapper;
import com.easybase.core.auth.domain.entity.ResourceAction;
import com.easybase.core.auth.service.ResourceActionService;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for resource action READ-ONLY operations.
 *
 * @author Akhash R
 */
@RequestMapping("/resource-actions")
@RequiredArgsConstructor
@RestController
public class ResourceActionController {

	/**
	 * Get all active resource actions for a specific resource type.
	 *
	 * @param resourceType the resource type
	 * @return list of active resource actions
	 */
	@GetMapping("/by-resource/{resourceType}/active")
	public List<ResourceActionDto> getActiveResourceActionsByType(
		@PathVariable String resourceType) {

		List<ResourceAction> actions =
			_resourceActionService.getActiveResourceActions(resourceType);

		return _resourceActionMapper.toDto(actions);
	}

	/**
	 * Get all active resource actions.
	 *
	 * @return list of all active resource actions
	 */
	@GetMapping("/active")
	public List<ResourceActionDto> getAllActiveResourceActions() {
		List<ResourceAction> actions =
			_resourceActionService.getAllActiveResourceActions();

		return _resourceActionMapper.toDto(actions);
	}

	/**
	 * Get a resource action by ID.
	 *
	 * @param actionId the action ID
	 * @return the resource action
	 */
	@GetMapping("/{actionId}")
	public ResourceActionDto getResourceActionById(
		@PathVariable UUID actionId) {

		ResourceAction resourceAction =
			_resourceActionService.getResourceActionById(actionId);

		return _resourceActionMapper.toDto(resourceAction);
	}

	/**
	 * Get all resource actions for a specific resource type.
	 *
	 * @param resourceType the resource type
	 * @return list of resource actions
	 */
	@GetMapping("/by-resource/{resourceType}")
	public List<ResourceActionDto> getResourceActionsByType(
		@PathVariable String resourceType) {

		List<ResourceAction> actions =
			_resourceActionService.getResourceActions(resourceType);

		return _resourceActionMapper.toDto(actions);
	}

	private final ResourceActionMapper _resourceActionMapper;
	private final ResourceActionService _resourceActionService;

}