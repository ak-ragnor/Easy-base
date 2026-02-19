/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.search;

/**
 * A single filter predicate consisting of a field, an operator, and a value.
 * A {@code null} value means IS NULL (with {@code EQ}) or IS NOT NULL (with
 * {@code NE}).
 *
 * @author Akhash R
 */
public class FilterCondition {

	public FilterCondition(
		String field, FilterOperator operator, String value) {

		_field = field;
		_operator = operator;
		_value = value;
	}

	public String getField() {
		return _field;
	}

	public FilterOperator getOperator() {
		return _operator;
	}

	public String getValue() {
		return _value;
	}

	private final String _field;
	private final FilterOperator _operator;
	private final String _value;

}