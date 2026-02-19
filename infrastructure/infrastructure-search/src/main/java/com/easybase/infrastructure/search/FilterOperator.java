/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.search;

/**
 * OData-style filter operators for query expressions.
 *
 * @author Akhash R
 */
public enum FilterOperator {

	/** String contains (case-insensitive): {@code contains(field,'value')} */
	CONTAINS("contains"),

	/** Equal: {@code eq} */
	EQ("eq"),

	/** Greater than or equal: {@code ge} */
	GE("ge"),

	/** Greater than: {@code gt} */
	GT("gt"),

	/** Less than or equal: {@code le} */
	LE("le"),

	/** Less than: {@code lt} */
	LT("lt"),

	/** Not equal: {@code ne} */
	NE("ne");

	FilterOperator(String keyword) {
		_keyword = keyword;
	}

	public static FilterOperator fromKeyword(String keyword) {
		for (FilterOperator operator : values()) {
			if (operator._keyword.equals(keyword)) {
				return operator;
			}
		}

		throw new IllegalArgumentException(
			"Unknown filter operator: " + keyword);
	}

	public String getKeyword() {
		return _keyword;
	}

	private final String _keyword;

}