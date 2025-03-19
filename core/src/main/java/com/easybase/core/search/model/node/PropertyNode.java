package com.easybase.core.search.model.node;

import com.easybase.core.search.visitor.QueryNodeVisitor;

/**
 * Represents a property/field reference.
 */
public class PropertyNode extends QueryNode {
    private final String name;

    public PropertyNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(QueryNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return name;
    }
}