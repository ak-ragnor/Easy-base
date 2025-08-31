/**
 * EasyBase Platform
 * Copyright (C) 2024 EasyBase
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.easybase.core.data.engine.util;

import java.util.UUID;

/**
 * Utility class for naming conventions and sanitization
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