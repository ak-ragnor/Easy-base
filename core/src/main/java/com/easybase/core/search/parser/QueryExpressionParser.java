package com.easybase.core.search.parser;

import com.easybase.core.search.exception.SearchException;
import com.easybase.core.search.model.node.*;
import com.easybase.core.search.model.operator.ComparisonOperator;
import com.easybase.core.search.model.operator.FunctionType;
import com.easybase.core.search.model.operator.LogicalOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for OData filter expressions.
 * This parser implements a recursive descent algorithm with support for:
 * - Logical operators (and, or, not)
 * - Comparison operators (eq, ne, gt, ge, lt, le, startsWith)
 * - Functions (contains)
 * - Parenthesized expressions
 */
public class QueryExpressionParser {
    private List<QueryTokenizer.Token> tokens;
    private int currentTokenIdx;

    /**
     * Parses a filter expression string into a QueryNode.
     *
     * @param expression The filter expression to parse
     * @return A QueryNode representing the parsed expression
     * @throws SearchException If the expression cannot be parsed
     */
    public QueryNode parse(String expression) throws SearchException {
        if (expression == null || expression.trim().isEmpty()) {
            return null;
        }

        // Tokenize the input
        QueryTokenizer tokenizer = new QueryTokenizer();
        tokens = tokenizer.tokenize(expression);
        currentTokenIdx = 0;

        // Parse the expression
        QueryNode result = _parseExpression();

        // Ensure we've consumed all tokens
        if (currentTokenIdx < tokens.size()) {
            throw new SearchException("Unexpected tokens after expression: " +
                    tokens.get(currentTokenIdx).getValue());
        }

        return result;
    }

    /**
     * Parses an expression, which is the top-level rule.
     * expression ::= logical_expression
     */
    private QueryNode _parseExpression() throws SearchException {
        return _parseLogicalExpression();
    }

    /**
     * Parses a logical expression (and, or, not).
     * logical_expression ::= logical_term (('or' logical_term)*
     */
    private QueryNode _parseLogicalExpression() throws SearchException {
        QueryNode left = _parseLogicalTerm();

        while (_match(QueryTokenizer.TOKEN_LOGICAL_OP) &&
                _currentToken().getValue().equals(LogicalOperator.OR.getSymbol())) {
            _advance(); // Consume the 'or'
            QueryNode right = _parseLogicalTerm();

            // If left is already an OR node, add the right term to it
            if (left instanceof LogicalNode &&
                    ((LogicalNode) left).getOperator() == LogicalOperator.OR) {

                LogicalNode orNode = (LogicalNode) left;
                List<QueryNode> operands = new ArrayList<>(orNode.getOperands());
                operands.add(right);
                left = new LogicalNode(LogicalOperator.OR, operands);
            } else {
                // Create a new OR node
                left = new LogicalNode(LogicalOperator.OR, List.of(left, right));
            }
        }

        return left;
    }

    /**
     * Parses a logical term, which is part of a logical expression.
     * logical_term ::= logical_factor (('and' logical_factor)*
     */
    private QueryNode _parseLogicalTerm() throws SearchException {
        QueryNode left = _parseLogicalFactor();

        while (_match(QueryTokenizer.TOKEN_LOGICAL_OP) &&
                _currentToken().getValue().equals(LogicalOperator.AND.getSymbol())) {
            _advance(); // Consume the 'and'
            QueryNode right = _parseLogicalFactor();

            // If left is already an AND node, add the right factor to it
            if (left instanceof LogicalNode &&
                    ((LogicalNode) left).getOperator() == LogicalOperator.AND) {

                LogicalNode andNode = (LogicalNode) left;
                List<QueryNode> operands = new ArrayList<>(andNode.getOperands());
                operands.add(right);
                left = new LogicalNode(LogicalOperator.AND, operands);
            } else {
                // Create a new AND node
                left = new LogicalNode(LogicalOperator.AND, List.of(left, right));
            }
        }

        return left;
    }

    /**
     * Parses a logical factor, which includes 'not' expressions and comparisons.
     * logical_factor ::= ['not'] (comparison | group)
     */
    private QueryNode _parseLogicalFactor() throws SearchException {
        // Check for 'not' operator
        if (_match(QueryTokenizer.TOKEN_LOGICAL_OP) &&
                _currentToken().getValue().equals(LogicalOperator.NOT.getSymbol())) {
            _advance(); // Consume the 'not'

            QueryNode operand = _parseLogicalFactor();
            return new LogicalNode(LogicalOperator.NOT, List.of(operand));
        }

        // Otherwise, it's a comparison or group
        if (_match(QueryTokenizer.TOKEN_LPAREN)) {
            return _parseGroup();
        }

        return _parseComparison();
    }

    /**
     * Parses a grouped expression (in parentheses).
     * group ::= '(' expression ')'
     */
    private QueryNode _parseGroup() throws SearchException {
        _consume(QueryTokenizer.TOKEN_LPAREN, "Expected '('");
        QueryNode expression = _parseExpression();
        _consume(QueryTokenizer.TOKEN_RPAREN, "Expected ')'");
        return new GroupNode(expression);
    }

    /**
     * Parses a comparison expression or function call.
     * comparison ::= property comparison_op value | function_call
     */
    private QueryNode _parseComparison() throws SearchException {
        // Check for a function call
        if (_match(QueryTokenizer.TOKEN_FUNCTION)) {
            return _parseFunctionCall();
        }

        // Parse the property
        PropertyNode property = _parseProperty();

        // Parse the comparison operator
        _consume(QueryTokenizer.TOKEN_COMPARISON_OP, "Expected comparison operator");
        ComparisonOperator operator = ComparisonOperator.fromSymbol(_previousToken().getValue());

        // Parse the value
        QueryNode value = _parseValue();

        return new ComparisonNode(property, operator, value);
    }

    /**
     * Parses a function call.
     * function_call ::= function_name '(' argument (',' argument)* ')'
     */
    private QueryNode _parseFunctionCall() throws SearchException {
        String functionName = _consume(QueryTokenizer.TOKEN_FUNCTION, "Expected function name").getValue();
        FunctionType functionType = FunctionType.fromName(functionName);

        _consume(QueryTokenizer.TOKEN_LPAREN, "Expected '(' after function name");

        List<QueryNode> arguments = new ArrayList<>();

        // Parse first argument
        arguments.add(_parseExpression());

        // Parse additional arguments if any
        while (_match(QueryTokenizer.TOKEN_COMMA)) {
            _advance(); // Consume the comma
            arguments.add(_parseExpression());
        }

        _consume(QueryTokenizer.TOKEN_RPAREN, "Expected ')' after function arguments");

        return new FunctionNode(functionType, arguments);
    }

    /**
     * Parses a property/field reference.
     * property ::= identifier
     */
    private PropertyNode _parseProperty() throws SearchException {
        String name = _consume(QueryTokenizer.TOKEN_IDENTIFIER, "Expected property name").getValue();
        return new PropertyNode(name);
    }

    /**
     * Parses a value (literal or property).
     * value ::= string | number | boolean | null | property
     */
    private QueryNode _parseValue() throws SearchException {
        if (_match(QueryTokenizer.TOKEN_STRING)) {
            String stringValue = _advance().getValue();
            // Remove the surrounding quotes
            return new LiteralNode(stringValue.substring(1, stringValue.length() - 1));
        }

        if (_match(QueryTokenizer.TOKEN_NUMBER)) {
            String numberValue = _advance().getValue();
            if (numberValue.contains(".")) {
                return new LiteralNode(Double.parseDouble(numberValue));
            } else {
                return new LiteralNode(Long.parseLong(numberValue));
            }
        }

        if (_match(QueryTokenizer.TOKEN_BOOLEAN)) {
            return new LiteralNode(Boolean.parseBoolean(_advance().getValue()));
        }

        if (_match(QueryTokenizer.TOKEN_NULL)) {
            _advance();
            return new LiteralNode(null);
        }

        // If it's not a literal, it must be a property
        return _parseProperty();
    }

    /**
     * Checks if the current token matches the given type.
     */
    private boolean _match(int tokenType) {
        if (currentTokenIdx >= tokens.size()) {
            return false;
        }

        return tokens.get(currentTokenIdx).getType() == tokenType;
    }

    /**
     * Returns the current token without advancing.
     */
    private QueryTokenizer.Token _currentToken() {
        if (currentTokenIdx >= tokens.size()) {
            return null;
        }

        return tokens.get(currentTokenIdx);
    }

    /**
     * Returns the previous token.
     */
    private QueryTokenizer.Token _previousToken() {
        if (currentTokenIdx <= 0) {
            return null;
        }

        return tokens.get(currentTokenIdx - 1);
    }

    /**
     * Advances to the next token and returns the previous one.
     */
    private QueryTokenizer.Token _advance() {
        if (currentTokenIdx >= tokens.size()) {
            return null;
        }

        return tokens.get(currentTokenIdx++);
    }

    /**
     * Consumes a token of the expected type, or throws an exception.
     */
    private QueryTokenizer.Token _consume(int tokenType, String errorMessage) throws SearchException {
        if (_match(tokenType)) {
            return _advance();
        }

        QueryTokenizer.Token current = _currentToken();
        throw new SearchException(errorMessage + " at position " +
                (current != null ? current.getPosition() : "end of input"));
    }
}