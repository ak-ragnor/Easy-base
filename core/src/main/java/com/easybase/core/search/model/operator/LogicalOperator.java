package com.easybase.core.search.model.operator;

/**
 * Enumeration of logical operators supported in filter expressions.
 */
public enum LogicalOperator {
    AND("and"),
    OR("or"),
    NOT("not");

    private final String symbol;

    LogicalOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static LogicalOperator fromSymbol(String symbol) {
        for (LogicalOperator operator : values()) {
            if (operator.symbol.equals(symbol)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("Unknown logical operator: " + symbol);
    }
}