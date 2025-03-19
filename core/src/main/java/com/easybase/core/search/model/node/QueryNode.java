package com.easybase.core.search.model.node;

import com.easybase.core.search.visitor.QueryNodeVisitor;

/**
 * Base class for all query expression nodes.
 */
public abstract class QueryNode {

    /**
     * Accepts a visitor that will process this node.
     *
     * @param visitor The visitor to process this node
     * @param <T> The return type of the visitor
     * @return The result of the visitor's processing
     */
    public abstract <T> T accept(QueryNodeVisitor<T> visitor);
}