package com.easybase.core.search.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.easybase.core.search.api.Page;
import com.easybase.core.search.api.Pagination;
import com.easybase.core.search.engine.ElasticsearchEngine;
import com.easybase.core.search.engine.SearchEngine;
import com.easybase.core.search.exception.SearchException;
import com.easybase.core.search.model.search.SearchCriteria;
import com.easybase.core.search.model.search.Sort;
import com.easybase.core.search.parser.SortParser;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for search operations.
 * This provides a static facade for search functionality.
 */
public class SearchUtil {
    private static SearchEngine searchEngine;

    /**
     * Initializes the SearchUtil with an Elasticsearch client.
     *
     * @param client The Elasticsearch client
     */
    public static void initialize(ElasticsearchClient client) {
        searchEngine = new ElasticsearchEngine(client);
    }

    /**
     * Sets the search engine to use.
     *
     * @param engine The search engine
     */
    public static void setSearchEngine(SearchEngine engine) {
        searchEngine = engine;
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
    public static <T> Page<T> search(
            String index,
            String search,
            String filter,
            String sort,
            int page,
            int size,
            Function<Map<String, Object>, T> resultMapper) throws SearchException {

        checkSearchEngine();

        // Parse sort expression
        List<Sort> sorts = Collections.emptyList();
        if (sort != null && !sort.trim().isEmpty()) {
            SortParser sortParser = new SortParser();
            sorts = sortParser.parse(sort);
        }

        // Build search criteria
        SearchCriteria criteria = SearchCriteria.builder()
                .searchText(search)
                .filter(filter)
                .sorts(sorts)
                .pagination(new Pagination(page, size))
                .build();

        // Execute search
        return searchEngine.search(index, criteria, resultMapper);
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
    public static <T> Page<T> search(
            String index,
            SearchCriteria criteria,
            Function<Map<String, Object>, T> resultMapper) throws SearchException {

        checkSearchEngine();
        return searchEngine.search(index, criteria, resultMapper);
    }

    /**
     * Checks that the search engine has been initialized.
     *
     * @throws SearchException If the search engine has not been initialized
     */
    private static void checkSearchEngine() throws SearchException {
        if (searchEngine == null) {
            throw new SearchException("SearchUtil has not been initialized with a search engine");
        }
    }
}