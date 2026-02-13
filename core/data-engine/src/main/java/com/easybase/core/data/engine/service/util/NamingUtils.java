/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.service.util;

import java.util.UUID;

/**
 * Utility class for naming conventions and sanitization
 *
 * @author Akhash R
 */
public class NamingUtils {

	/**
	 * Generate table name with tenant prefix
	 */
	public static String getTableName(
		UUID tenantId, String collectionName) {

		String tenantIdString = tenantId.toString();

		String tenantUUID = tenantIdString.replace("-", "_");

		return tenantUUID + "_" + sanitizeCollectionName(collectionName);
	}

	/**
	 * Sanitize collection name to be database-safe
	 */
	public static String sanitizeCollectionName(String name) {
		if (name == null) {
			throw new IllegalArgumentException(
				"Collection name cannot be null or empty");
		}

		String trimmedName = name.trim();

		if (trimmedName.isEmpty()) {
			throw new IllegalArgumentException(
				"Collection name cannot be null or empty");
		}

		String lower = trimmedName.toLowerCase();

		String replacedSpaces = lower.replaceAll("[\\s-]+", "_");

		String sanitized = replacedSpaces.replaceAll("[^a-z0-9_]", "");

		if (!sanitized.matches("^[a-z].*")) {
			sanitized = "tbl_" + sanitized;
		}

		if (sanitized.length() > 63) {
			sanitized = sanitized.substring(0, 63);
		}

		return sanitized;
	}

}