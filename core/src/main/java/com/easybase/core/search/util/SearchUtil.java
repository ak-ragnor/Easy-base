package com.easybase.core.search.util;

import com.easybase.core.search.api.Page;
import com.easybase.core.search.api.Pagination;
import com.easybase.core.search.engine.SearchEngine;
import com.easybase.core.search.exception.SearchException;
import com.easybase.core.search.model.search.SearchCriteria;
import com.easybase.core.search.model.search.Sort;
import com.easybase.core.search.parser.SortParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Component for search operations.
 * Provides centralized search functionality, leveraging DataSourceManager.
 */
@Component
public class SearchUtil {
    private static final Logger logger = LoggerFactory.getLogger(SearchUtil.class);

    private final SearchEngine _searchEngine;
    private final SortParser _sortParser;

    /**
     * Creates a new SearchUtil with required dependencies.
     *
     * @param searchEngine The search engine implementation
     */
    public SearchUtil(SearchEngine searchEngine) {
        this._searchEngine = searchEngine;
        this._sortParser = new SortParser();
    }

    /**
     * Executes a search with the given parameters.
     *
     * @param index The index to search
     * @param search The search text
     * @param filter The filter expression
     * @param sort The sort expression
     * @param page The page number
     * @param size The page size
     * @param resultMapper The function to map search hits to result objects
     * @param <T> The type of the result objects
     * @return A page of search results
     * @throws SearchException If the search operation fails
     */
    public <T> Page<T> search(
            String index,
            String search,
            String filter,
            String sort,
            int page,
            int size,
            Function<Map<String, Object>, T> resultMapper) throws SearchException {

        // Parse sort expression
        List<Sort> sorts = Collections.emptyList();
        if (sort != null && !sort.trim().isEmpty()) {
            sorts = _sortParser.parse(sort);
        }

        // Build search criteria
        SearchCriteria criteria = SearchCriteria.builder()
                .searchText(search)
                .filter(filter)
                .sorts(sorts)
                .pagination(new Pagination(page, size))
                .build();

        // Execute search
        return _searchEngine.search(index, criteria, resultMapper);
    }

    /**
     * Executes a search with the given criteria.
     *
     * @param index The index to search
     * @param criteria The search criteria
     * @param resultMapper The function to map search hits to result objects
     * @param <T> The type of the result objects
     * @return A page of search results
     * @throws SearchException If the search operation fails
     */
    public <T> Page<T> search(
            String index,
            SearchCriteria criteria,
            Function<Map<String, Object>, T> resultMapper) throws SearchException {

        return _searchEngine.search(index, criteria, resultMapper);
    }

    /**
     * Verifies the existence of an index before search operations.
     * Uses DataSourceManager's caching capabilities.
     *
     * @param index The index name to verify
     * @return true if the index exists
     */
    public boolean verifyIndex(String index) {

        return _searchEngine.verifyIndex(index);
    }
}