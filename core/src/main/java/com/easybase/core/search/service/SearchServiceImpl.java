package com.easybase.core.search.service;

import com.easybase.core.search.api.Page;
import com.easybase.core.search.exception.SearchException;
import com.easybase.core.search.model.search.SearchCriteria;
import com.easybase.core.search.util.SearchUtil;
import com.easybase.core.storage.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of SearchService that uses SearchUtil and DataSourceManager.
 */
@Service
public class SearchServiceImpl implements SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final SearchUtil searchUtil;
    private final DataSourceManager dataSourceManager;

    public SearchServiceImpl(SearchUtil searchUtil, DataSourceManager dataSourceManager) {
        this.searchUtil = searchUtil;
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public <T> Page<T> search(String index, SearchCriteria criteria,
                              Function<Map<String, Object>, T> resultMapper) throws SearchException {
        // Verify the index exists before searching
        if (!searchUtil.verifyIndex(index)) {
            logger.warn("Search index '{}' does not exist", index);
            // Return empty page rather than throw an exception
            return Page.of(
                    java.util.Collections.emptyList(),
                    criteria.getPagination(),
                    0
            );
        }

        // Apply permission filtering if table exists in database
        if (dataSourceManager.tableExists(index)) {
            String filter = criteria.getFilter();
            String permissionFilter = _buildPermissionFilter(index);

            // Combine filters if both exist
            if (filter != null && !filter.isEmpty() && permissionFilter != null) {
                filter = "(" + filter + ") and " + permissionFilter;
            } else if (permissionFilter != null) {
                filter = permissionFilter;
            }

            // Create new criteria with updated filter
            if (filter != null && !filter.equals(criteria.getFilter())) {
                criteria = SearchCriteria.builder()
                        .searchText(criteria.getSearchText())
                        .filter(filter)
                        .sorts(criteria.getSorts())
                        .pagination(criteria.getPagination())
                        .build();
            }
        }

        // Execute the search
        return searchUtil.search(index, criteria, resultMapper);
    }

    /**
     * Builds a permission filter for the given collection.
     * This can be extended to implement row-level security.
     *
     * @param collection The collection name
     * @return The permission filter string, or null if no filtering needed
     */
    private String _buildPermissionFilter(String collection) {
        // This is a placeholder for actual permission logic
        // In a real implementation, this would check the user's permissions
        // and return a filter expression accordingly
        return null;
    }
}