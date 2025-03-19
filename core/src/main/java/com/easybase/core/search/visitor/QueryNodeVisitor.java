package com.easybase.core.search.visitor;

import com.easybase.core.search.model.node.*;

/**
 * Visitor interface for processing query nodes.
 * Implements the Visitor design pattern.
 *
 * @param <T> The return type of the visitor
 */
public interface QueryNodeVisitor<T> {
    /**
     * Visits a comparison node.
     *
     * @param node The node to visit
     * @return The result of the visit
     */
    T visit(ComparisonNode node);

    /**
     * Visits a logical node.
     *
     * @param node The node to visit
     * @return The result of the visit
     */
    T visit(LogicalNode node);

    /**
     * Visits a group node.
     *
     * @param node The node to visit
     * @return The result of the visit
     */
    T visit(GroupNode node);

    /**
     * Visits a function node.
     *
     * @param node The node to visit
     * @return The result of the visit
     */
    T visit(FunctionNode node);

    /**
     * Visits a property node.
     *
     * @param node The node to visit
     * @return The result of the visit
     */
    T visit(PropertyNode node);

    /**
     * Visits a literal node.
     *
     * @param node The node to visit
     * @return The result of the visit
     */
    T visit(LiteralNode node);
}