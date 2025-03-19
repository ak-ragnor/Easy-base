package com.easybase.core.search.model.node;

import com.easybase.core.search.visitor.QueryNodeVisitor;

/**
 * Represents a parenthesized expression.
 */
public class GroupNode extends QueryNode {
    private final QueryNode child;

    public GroupNode(QueryNode child) {
        this.child = child;
    }

    public QueryNode getChild() {
        return child;
    }

    @Override
    public <T> T accept(QueryNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "(" + child + ")";
    }
}