package com.easybase.core.search.builder;

import com.easybase.core.search.api.Pagination;
import com.easybase.core.search.model.node.QueryNode;
import com.easybase.core.search.model.search.Sort;

import java.util.List;

/**
 * Interface for building backend-specific search queries.
 * This follows the Builder pattern to create complex search queries.
 *
 * @param <T> The type of the built query
 */
public interface QueryBuilder<T> {

    /**
     * Adds a search text (keyword search) to the query.
     *
     * @param searchText The search text
     * @return This builder
     */
    QueryBuilder<T> withSearchText(String searchText);

    /**
     * Adds a filter to the query.
     *
     * @param filterNode The filter AST node
     * @return This builder
     */
    QueryBuilder<T> withFilter(QueryNode filterNode);

    /**
     * Adds sort criteria to the query.
     *
     * @param sorts The sort criteria
     * @return This builder
     */
    QueryBuilder<T> withSorts(List<Sort> sorts);

    /**
     * Adds pagination to the query.
     *
     * @param pagination The pagination parameters
     * @return This builder
     */
    QueryBuilder<T> withPagination(Pagination pagination);

    /**
     * Builds the query using all the configured options.
     *
     * @return The built query
     */
    T build();
}