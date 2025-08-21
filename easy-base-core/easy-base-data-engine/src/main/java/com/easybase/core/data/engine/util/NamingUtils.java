package com.easybase.core.data.engine.util;

import java.util.UUID;

/**
 * Utility class for naming conventions and sanitization
 */
public class NamingUtils {

	private NamingUtils() {
	}

	/**
	 * Sanitize collection name to be database-safe
	 */
	public static String sanitizeCollectionName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Collection name cannot be null or empty");
		}

		String sanitized = name.toLowerCase().replaceAll("[\\s-]+", "_")
				.replaceAll("[^a-z0-9_]", "");

		if (!sanitized.matches("^[a-z].*")) {
			sanitized = "tbl_" + sanitized;
		}

		if (sanitized.length() > 63) {
			sanitized = sanitized.substring(0, 63);
		}

		return sanitized;
	}

	/**
	 * Generate table name with tenant prefix
	 */
	public static String generateTableName(UUID tenantId,
			String collectionName) {
		String sanitizedCollectionName = sanitizeCollectionName(collectionName);
		String tenantPrefix = tenantId.toString().replace("-", "").substring(0,
				8);

		return tenantPrefix + "_" + sanitizedCollectionName;
	}

	/**
	 * Sanitize attribute name to be database-safe
	 */
	public static String sanitizeAttributeName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Attribute name cannot be null or empty");
		}

		String sanitized = name.replaceAll("[^a-zA-Z0-9_]", "");

		if (!sanitized.matches("^[a-zA-Z].*")) {
			sanitized = "attr_" + sanitized;
		}

		return sanitized;
	}
}
