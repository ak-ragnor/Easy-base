/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.search;

import java.util.List;

import lombok.Data;

/**
 * A node in a filter expression tree. Leaf nodes hold a {@link FilterCondition};
 * non-leaf nodes combine children with AND or OR logic.
 *
 * @author Akhash R
 */
@Data
public class FilterNode {

	public static FilterNode and(List<FilterNode> children) {
		FilterNode node = new FilterNode();

		node._logic = LogicOperator.AND;
		node._children = children;

		return node;
	}

	public static FilterNode leaf(FilterCondition condition) {
		FilterNode node = new FilterNode();

		node._condition = condition;

		return node;
	}

	public static FilterNode or(List<FilterNode> children) {
		FilterNode node = new FilterNode();

		node._logic = LogicOperator.OR;
		node._children = children;

		return node;
	}

	public List<FilterNode> getChildren() {
		return _children;
	}

	public FilterCondition getCondition() {
		return _condition;
	}

	public LogicOperator getLogic() {
		return _logic;
	}

	public boolean isLeaf() {
		if (_condition != null) {
			return true;
		}

		return false;
	}

	public enum LogicOperator {

		AND, OR

	}

	private List<FilterNode> _children;
	private FilterCondition _condition;
	private LogicOperator _logic;

}