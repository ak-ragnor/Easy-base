package com.easybase.core.search.model.node;

import com.easybase.core.search.model.operator.ComparisonOperator;
import com.easybase.core.search.visitor.QueryNodeVisitor;

/**
 * Represents a comparison expression like "field eq value".
 */
public class ComparisonNode extends QueryNode {
    private final PropertyNode property;
    private final ComparisonOperator operator;
    private final QueryNode value;

    public ComparisonNode(PropertyNode property, ComparisonOperator operator, QueryNode value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
    }

    public PropertyNode getProperty() {
        return property;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public QueryNode getValue() {
        return value;
    }

    @Override
    public <T> T accept(QueryNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return property + " " + operator + " " + value;
    }
}