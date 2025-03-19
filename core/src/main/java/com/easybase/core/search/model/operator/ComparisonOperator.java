package com.easybase.core.search.model.operator;

/**
 * Enumeration of comparison operators supported in filter expressions.
 */
public enum ComparisonOperator {
    EQ("eq"),       // Equal
    NE("ne"),       // Not equal
    GT("gt"),       // Greater than
    GE("ge"),       // Greater than or equal
    LT("lt"),       // Less than
    LE("le"),       // Less than or equal
    STARTS_WITH("startsWith");  // String starts with

    private final String symbol;

    ComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static ComparisonOperator fromSymbol(String symbol) {
        for (ComparisonOperator operator : values()) {
            if (operator.symbol.equals(symbol)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("Unknown comparison operator: " + symbol);
    }
}