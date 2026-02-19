/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.search;

/**
 * Represents a single sort directive: a field name and direction.
 *
 * @author Akhash R
 */
public class SortField {

	public SortField(boolean descending, String field) {
		_descending = descending;
		_field = field;
	}

	public String getField() {
		return _field;
	}

	public boolean isDescending() {
		return _descending;
	}

	private final boolean _descending;
	private final String _field;

}