package com.easybase.core.search.model.node;

import com.easybase.core.search.model.operator.FunctionType;
import com.easybase.core.search.visitor.QueryNodeVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a function call like contains(field, value).
 */
public class FunctionNode extends QueryNode {
    private final FunctionType functionType;
    private final List<QueryNode> arguments;

    public FunctionNode(FunctionType functionType, List<QueryNode> arguments) {
        this.functionType = functionType;
        this.arguments = new ArrayList<>(arguments);
    }

    public FunctionType getFunctionType() {
        return functionType;
    }

    public List<QueryNode> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public <T> T accept(QueryNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(functionType.getName()).append("(");

        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(arguments.get(i));
        }

        sb.append(")");
        return sb.toString();
    }
}