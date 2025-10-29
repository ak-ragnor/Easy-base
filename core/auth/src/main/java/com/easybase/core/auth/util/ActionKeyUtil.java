/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.util;

import java.util.Locale;

/**
 * @author Akhash R
 */
public class ActionKeyUtil {

	public static String getActionKey(String table, String action) {
		if ((table == null) || (action == null)) {
			throw new IllegalArgumentException(
				"table and action cannot be null");
		}

		return table.toUpperCase(Locale.ROOT) + ":" +
			action.toUpperCase(Locale.ROOT);
	}

	private ActionKeyUtil() {
		throw new UnsupportedOperationException("Utility class");
	}

}