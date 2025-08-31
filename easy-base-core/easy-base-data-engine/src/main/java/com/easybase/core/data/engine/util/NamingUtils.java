/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.util;

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
	public static String generateTableName(
		UUID tenantId, String collectionName) {

		String sanitizedCollectionName = sanitizeCollectionName(collectionName);

		String tenantIdString = tenantId.toString();

		String tenantIdNoDash = tenantIdString.replace("-", "");

		String tenantPrefix = tenantIdNoDash.substring(0, 8);

		return tenantPrefix + "_" + sanitizedCollectionName;
	}

	/**
	 * Sanitize attribute name to be database-safe
	 */
	public static String sanitizeAttributeName(String name) {
		if (name == null) {
			throw new IllegalArgumentException(
				"Attribute name cannot be null or empty");
		}

		String trimmedName = name.trim();

		if (trimmedName.isEmpty()) {
			throw new IllegalArgumentException(
				"Attribute name cannot be null or empty");
		}

		String sanitized = trimmedName.replaceAll("[^a-zA-Z0-9_]", "");

		if (!sanitized.matches("^[a-zA-Z].*")) {
			sanitized = "attr_" + sanitized;
		}

		return sanitized;
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