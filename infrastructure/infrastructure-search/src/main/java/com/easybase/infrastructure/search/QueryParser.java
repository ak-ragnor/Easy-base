/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.infrastructure.search;

import com.easybase.common.exception.InvalidRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses OData-style filter expressions into a {@link FilterNode} tree and
 * sort strings into a list of {@link SortField}s.
 *
 * <p>Filter grammar:
 * <pre>
 * expr      := orExpr
 * orExpr    := andExpr ('or' andExpr)*
 * andExpr   := notExpr ('and' notExpr)*
 * notExpr   := 'not' '(' expr ')' | '(' expr ')' | atom
 * atom      := contains(field,'value') | field op value
 * op        := eq | ne | gt | ge | lt | le
 * value     := 'string' | null | unquoted
 * </pre>
 *
 * @author Akhash R
 */
public class QueryParser {

	public static FilterNode parseFilter(String filter) {
		if ((filter == null) || filter.isBlank()) {
			return null;
		}

		ODataFilterParser parser = new ODataFilterParser(filter.trim());

		FilterNode result = parser._parseExpr();

		if (parser._pos < parser._tokens.size()) {
			throw new InvalidRequestException(
				"Unexpected token at position " + parser._pos + ": " +
					parser._tokens.get(parser._pos));
		}

		return result;
	}

	public static List<SortField> parseSort(String sort) {
		if ((sort == null) || sort.isBlank()) {
			return List.of();
		}

		List<SortField> sortFields = new ArrayList<>();

		String[] parts = sort.split(",");

		for (String part : parts) {
			String trimmed = part.trim();

			if (trimmed.isEmpty()) {
				continue;
			}

			if (trimmed.startsWith("-")) {
				String fieldName = trimmed.substring(1);

				_validateFieldName(fieldName);

				sortFields.add(new SortField(true, fieldName));
			}
			else {
				String fieldName =
					trimmed.startsWith("+") ? trimmed.substring(1) : trimmed;

				_validateFieldName(fieldName);

				sortFields.add(new SortField(false, fieldName));
			}
		}

		return sortFields;
	}

	private static void _validateFieldName(String fieldName) {
		if ((fieldName == null) || fieldName.isBlank()) {
			throw new InvalidRequestException("Field name cannot be blank");
		}

		Matcher matcher = _fieldNamePattern.matcher(fieldName);

		if (!matcher.matches()) {
			throw new InvalidRequestException(
				"Invalid field name: " + fieldName);
		}
	}

	private static final Pattern _fieldNamePattern = Pattern.compile(
		"^[a-zA-Z_][a-zA-Z0-9_]*$");

	/**
	 * Recursive-descent parser over a token list.
	 */
	private static class ODataFilterParser {

		ODataFilterParser(String input) {
			_tokens = _tokenize(input);
		}

		/** expr := orExpr */
		FilterNode _parseExpr() {
			return _parseOrExpr();
		}

		/** orExpr := andExpr ('or' andExpr)* */
		FilterNode _parseOrExpr() {
			FilterNode left = _parseAndExpr();

			while (_peek("or")) {
				_consume("or");

				FilterNode right = _parseAndExpr();

				left = _mergeBinary(FilterNode.LogicOperator.OR, left, right);
			}

			return left;
		}

		/** andExpr := notExpr ('and' notExpr)* */
		FilterNode _parseAndExpr() {
			FilterNode left = _parseNotExpr();

			while (_peek("and")) {
				_consume("and");

				FilterNode right = _parseNotExpr();

				left = _mergeBinary(FilterNode.LogicOperator.AND, left, right);
			}

			return left;
		}

		/** notExpr := 'not' '(' expr ')' | '(' expr ')' | atom */
		FilterNode _parseNotExpr() {
			if (_peek("not")) {
				_consume("not");
				_consume("(");

				FilterNode inner = _parseExpr();

				_consume(")");

				return _negate(inner);
			}

			if (_peek("(")) {
				_consume("(");

				FilterNode inner = _parseExpr();

				_consume(")");

				return inner;
			}

			return _parseAtom();
		}

		/** atom := contains(field,'value') | field op value */
		FilterNode _parseAtom() {
			String token = _current();

			if ("contains".equalsIgnoreCase(token)) {
				return _parseContains();
			}

			return _parseFieldOp();
		}

		/** contains(field,'value') → FilterCondition(field, CONTAINS, value) */
		FilterNode _parseContains() {
			_consume("contains");
			_consume("(");

			String field = _current();

			_advance();
			_validateFieldName(field);
			_consume(",");

			String value = _parseStringValue();

			_consume(")");

			return FilterNode.leaf(
				new FilterCondition(field, FilterOperator.CONTAINS, value));
		}

		/** field op value → FilterCondition */
		FilterNode _parseFieldOp() {
			String field = _current();

			_advance();
			_validateFieldName(field);

			String opToken = _current();

			_advance();

			FilterOperator operator;

			try {
				operator = FilterOperator.fromKeyword(opToken.toLowerCase());
			}
			catch (IllegalArgumentException illegalArgumentException) {
				throw new InvalidRequestException(
					"Unknown filter operator: " + opToken,
					illegalArgumentException);
			}

			String value = _parseValue();

			return FilterNode.leaf(new FilterCondition(field, operator, value));
		}

		/** value := 'string' | null | unquoted */
		String _parseValue() {
			String token = _current();

			if (token.startsWith("'") && token.endsWith("'") &&
				(token.length() >= 2)) {

				_advance();

				return token.substring(1, token.length() - 1);
			}

			if ("null".equalsIgnoreCase(token)) {
				_advance();

				return null;
			}

			_advance();

			return token;
		}

		/** Parse a single-quoted string and return its content. */
		String _parseStringValue() {
			String token = _current();

			if (token.startsWith("'") && token.endsWith("'") &&
				(token.length() >= 2)) {

				_advance();

				return token.substring(1, token.length() - 1);
			}

			throw new InvalidRequestException(
				"Expected single-quoted string, got: " + token);
		}

		// ── helpers ──────────────────────────────────────────────────────────

		void _advance() {
			_pos++;
		}

		void _consume(String expected) {
			String token = _current();

			if (!expected.equalsIgnoreCase(token)) {
				throw new InvalidRequestException(
					"Expected '" + expected + "' but got '" + token + "'");
			}

			_advance();
		}

		String _current() {
			if (_pos >= _tokens.size()) {
				throw new InvalidRequestException(
					"Unexpected end of filter expression");
			}

			return _tokens.get(_pos);
		}

		boolean _peek(String expected) {
			if (_pos >= _tokens.size()) {
				return false;
			}

			return expected.equalsIgnoreCase(_tokens.get(_pos));
		}

		/**
		 * Negate a node: wraps with a NOT marker via a single-child OR node
		 * that signals logical negation to the engine. Since the filter tree
		 * does not have an explicit NOT node type we represent
		 * {@code not (expr)} by inverting the leaf condition operator for
		 * simple cases, and for compound expressions we throw — callers
		 * should use {@code not (leaf)} patterns.
		 */
		FilterNode _negate(FilterNode node) {
			if (node.isLeaf()) {
				FilterCondition c = node.getCondition();
				FilterOperator negated;

				switch (c.getOperator()) {
					case EQ:
						negated = FilterOperator.NE;

						break;
					case NE:
						negated = FilterOperator.EQ;

						break;
					case GT:
						negated = FilterOperator.LE;

						break;
					case GE:
						negated = FilterOperator.LT;

						break;
					case LT:
						negated = FilterOperator.GE;

						break;
					case LE:
						negated = FilterOperator.GT;

						break;
					default:
						throw new InvalidRequestException(
							"Cannot negate operator: " + c.getOperator());
				}

				return FilterNode.leaf(
					new FilterCondition(c.getField(), negated, c.getValue()));
			}

			if (node.getLogic() == FilterNode.LogicOperator.AND) {
				List<FilterNode> negatedChildren = new ArrayList<>();

				for (FilterNode child : node.getChildren()) {
					negatedChildren.add(_negate(child));
				}

				return FilterNode.or(negatedChildren);
			}

			List<FilterNode> negatedChildren = new ArrayList<>();

			for (FilterNode child : node.getChildren()) {
				negatedChildren.add(_negate(child));
			}

			return FilterNode.and(negatedChildren);
		}

		/** Merge two nodes with a logic operator, collapsing same-operator chains. */
		FilterNode _mergeBinary(
			FilterNode.LogicOperator op, FilterNode left, FilterNode right) {

			if (!left.isLeaf() && (left.getLogic() == op)) {
				List<FilterNode> children = new ArrayList<>(left.getChildren());

				children.add(right);

				if (op == FilterNode.LogicOperator.AND) {
					return FilterNode.and(children);
				}

				return FilterNode.or(children);
			}

			List<FilterNode> children = new ArrayList<>();

			children.add(left);
			children.add(right);

			if (op == FilterNode.LogicOperator.AND) {
				return FilterNode.and(children);
			}

			return FilterNode.or(children);
		}

		/**
		 * Tokenize the filter string into a list of tokens:
		 * keywords, identifiers, single-quoted strings, parentheses, commas.
		 */
		List<String> _tokenize(String input) {
			List<String> tokens = new ArrayList<>();

			int i = 0;
			int len = input.length();

			while (i < len) {
				char c = input.charAt(i);

				if (Character.isWhitespace(c)) {
					i++;

					continue;
				}

				if ((c == '(') || (c == ')') || (c == ',')) {
					tokens.add(String.valueOf(c));
					i++;

					continue;
				}

				if (c == '\'') {
					int start = i;

					i++;

					while ((i < len) && (input.charAt(i) != '\'')) {
						if ((input.charAt(i) == '\\') && ((i + 1) < len)) {
							i++;
						}

						i++;
					}

					if (i >= len) {
						throw new InvalidRequestException(
							"Unterminated string literal in filter");
					}

					i++;

					tokens.add(input.substring(start, i));

					continue;
				}

				int start = i;

				while ((i < len) && !Character.isWhitespace(input.charAt(i)) &&
					   (input.charAt(i) != '(') && (input.charAt(i) != ')') &&
					   (input.charAt(i) != ',') && (input.charAt(i) != '\'')) {

					i++;
				}

				tokens.add(input.substring(start, i));
			}

			return tokens;
		}

		int _pos;
		final List<String> _tokens;

	}

}