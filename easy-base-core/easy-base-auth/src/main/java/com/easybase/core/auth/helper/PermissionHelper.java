/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.helper;

import com.easybase.core.auth.entity.ResourceAction;
import com.easybase.core.auth.service.ResourceActionLocalService;
import com.easybase.core.auth.util.BitMaskUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * Helper class for permission-related operations.
 * Handles conversion between action keys and bit values, validation, and mask calculations.
 *
 * @author Akhash R
 */
@Component
@RequiredArgsConstructor
public class PermissionHelper {

	/**
	 * Calculate a permission mask from a list of action keys.
	 *
	 * @param resourceType the resource type
	 * @param actionKeys list of action keys (e.g., ["create", "read"])
	 * @return the combined permission mask
	 * @throws IllegalArgumentException if any action is invalid or inactive
	 */
	public long calculatePermissionMask(
		String resourceType, List<String> actionKeys) {

		if ((actionKeys == null) || actionKeys.isEmpty()) {
			return 0L;
		}

		int[] bitValues = convertActionKeysToBitValues(
			resourceType, actionKeys);

		return BitMaskUtil.combineBits(bitValues);
	}

	/**
	 * Convert action keys to bit values.
	 *
	 * @param resourceType the resource type
	 * @param actionKeys list of action keys (e.g., ["create", "read"])
	 * @return array of bit values
	 * @throws IllegalArgumentException if any action is invalid or inactive
	 */
	public int[] convertActionKeysToBitValues(
		String resourceType, List<String> actionKeys) {

		List<Integer> bitValues = new ArrayList<>();

		for (String actionKey : actionKeys) {
			ResourceAction action =
				_resourceActionLocalService.getResourceAction(
					resourceType, actionKey);

			if (action == null) {
				throw new IllegalArgumentException(
					"Unknown action: " + resourceType + "." + actionKey);
			}

			if (!action.isActive()) {
				throw new IllegalArgumentException(
					"Action is not active: " + resourceType + "." + actionKey);
			}

			bitValues.add(action.getBitValue());
		}

		Stream<Integer> bitValuesStream = bitValues.stream();

		return bitValuesStream.mapToInt(
			Integer::intValue
		).toArray();
	}

	/**
	 * Convert a permission mask to action keys.
	 *
	 * @param resourceType the resource type
	 * @param permissionsMask the permissions mask
	 * @return list of action keys that are granted
	 */
	public List<String> convertBitValuesToActionKeys(
		String resourceType, long permissionsMask) {

		List<String> actionKeys = new ArrayList<>();

		List<ResourceAction> actions =
			_resourceActionLocalService.getResourceActions(resourceType);

		for (ResourceAction action : actions) {
			if (BitMaskUtil.hasBit(permissionsMask, action.getBitValue())) {
				actionKeys.add(action.getActionKey());
			}
		}

		return actionKeys;
	}

	/**
	 * Validate that all action keys exist and are active.
	 *
	 * @param resourceType the resource type
	 * @param actionKeys list of action keys to validate
	 * @throws IllegalArgumentException if any action is invalid or inactive
	 */
	public void validateActions(String resourceType, List<String> actionKeys) {
		for (String actionKey : actionKeys) {
			ResourceAction action =
				_resourceActionLocalService.getResourceAction(
					resourceType, actionKey);

			if (action == null) {
				throw new IllegalArgumentException(
					"Unknown action: " + resourceType + "." + actionKey);
			}

			if (!action.isActive()) {
				throw new IllegalArgumentException(
					"Action is not active: " + resourceType + "." + actionKey);
			}
		}
	}

	private final ResourceActionLocalService _resourceActionLocalService;

}