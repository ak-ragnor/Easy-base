package com.easybase.core.search.model.node;

import com.easybase.core.search.visitor.QueryNodeVisitor;

/**
 * Represents a literal value (string, number, boolean, null).
 */
public class LiteralNode extends QueryNode {
    private final Object value;

    public LiteralNode(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public boolean isNull() {
        return value == null;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    @Override
    public <T> T accept(QueryNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (value == null) {
            return "null";
        }

        if (value instanceof String) {
            return "'" + value + "'";
        }

        return value.toString();
    }
}