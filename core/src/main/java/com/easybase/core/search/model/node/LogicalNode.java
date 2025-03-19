package com.easybase.core.search.model.node;

import com.easybase.core.search.model.operator.LogicalOperator;
import com.easybase.core.search.visitor.QueryNodeVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a logical expression (AND, OR, NOT).
 */
public class LogicalNode extends QueryNode {
    private final LogicalOperator operator;
    private final List<QueryNode> operands;

    public LogicalNode(LogicalOperator operator, List<QueryNode> operands) {
        this.operator = operator;
        this.operands = new ArrayList<>(operands);
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    public List<QueryNode> getOperands() {
        return Collections.unmodifiableList(operands);
    }

    @Override
    public <T> T accept(QueryNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        if (operator == LogicalOperator.NOT) {
            return operator + " " + operands.get(0);
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < operands.size(); i++) {
            if (i > 0) {
                sb.append(" ").append(operator).append(" ");
            }
            sb.append(operands.get(i));
        }

        return sb.toString();
    }
}