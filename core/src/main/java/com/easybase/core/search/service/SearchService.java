package com.easybase.core.search.service;

import com.easybase.core.search.api.Page;
import com.easybase.core.search.exception.SearchException;
import com.easybase.core.search.model.search.SearchCriteria;

import java.util.Map;
import java.util.function.Function;

/**
 * Service interface for executing search operations.
 */
public interface SearchService {

    /**
     * Executes a search using the given criteria and maps the results to the given type.
     *
     * @param index The index to search
     * @param criteria The search criteria
     * @param resultMapper The function to map search hits to result objects
     * @param <T> The type of the result objects
     * @return A page of search results
     * @throws SearchException If the search operation fails
     */
    <T> Page<T> search(String index, SearchCriteria criteria,
                       Function<Map<String, Object>, T> resultMapper) throws SearchException;
}