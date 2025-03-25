package com.easybase.core.search.engine;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.easybase.core.search.api.Page;
import com.easybase.core.search.builder.ElasticsearchQueryBuilder;
import com.easybase.core.search.exception.SearchException;
import com.easybase.core.search.model.node.QueryNode;
import com.easybase.core.search.model.search.SearchCriteria;
import com.easybase.core.search.parser.QueryExpressionParser;
import com.easybase.core.search.visitor.QueryBuilderVisitor;
import com.easybase.core.storage.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Elasticsearch implementation of the SearchEngine interface.
 * Uses DataSourceManager for resource management.
 */
@Service
public class ElasticsearchEngine implements SearchEngine {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchEngine.class);

    private final DataSourceManager _dataSourceManager;
    private final QueryExpressionParser _parser;
    private final QueryBuilderVisitor _visitor;

    /**
     * Creates a new ElasticsearchEngine using DataSourceManager.
     *
     * @param dataSourceManager The centralized data source manager
     */
    public ElasticsearchEngine(DataSourceManager dataSourceManager) {
        this._dataSourceManager = dataSourceManager;
        this._parser = new QueryExpressionParser();
        this._visitor = new QueryBuilderVisitor();
    }

    @Override
    public <T> Page<T> search(String index, SearchCriteria criteria,
                              Function<Map<String, Object>, T> resultMapper) throws SearchException {
        try {
            QueryNode filterNode = null;
            if (criteria.getFilter() != null && !criteria.getFilter().trim().isEmpty()) {
                filterNode = _parser.parse(criteria.getFilter());
            }

            // Build the search request
            SearchRequest searchRequest = new ElasticsearchQueryBuilder(_visitor)
                    .withSearchText(criteria.getSearchText())
                    .withFilter(filterNode)
                    .withSorts(criteria.getSorts())
                    .withPagination(criteria.getPagination())
                    .build();

            // Log the search request at debug level
            logger.debug("Executing search on index {}: {}", index, searchRequest);

            // Execute the search using DataSourceManager's ElasticsearchClient
            SearchResponse<Map> response = _dataSourceManager.getElasticsearchClient()
                    .search(searchRequest, Map.class);

            // Process the results
            List<T> items = new ArrayList<>();
            for (Hit<Map> hit : response.hits().hits()) {
                if (hit.source() != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> source = hit.source();

                    // Add document ID to the source if not present
                    if (!source.containsKey("id") && hit.id() != null) {
                        source.put("id", hit.id());
                    }

                    T item = resultMapper.apply(source);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }

            // Get the total number of results
            long totalHits = response.hits().total() != null ?
                    response.hits().total().value() : 0;

            return Page.of(items, criteria.getPagination(), totalHits);

        } catch (IOException e) {
            String errorMessage = String.format(
                    "Failed to execute search on index %s: %s", index, e.getMessage());
            logger.error(errorMessage, e);
            throw new SearchException(errorMessage, e);
        }
    }

    @Override
    public boolean verifyIndex(String index) {
        try {
            return _dataSourceManager.getElasticsearchClient().indices()
                    .exists(e -> e.index(index))
                    .value();
        } catch (IOException e) {
            logger.warn("Failed to verify index existence: {}", e.getMessage());
            return false;
        }
    }
}