package com.easybase.core.search.engine;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Elasticsearch implementation of the SearchEngine interface.
 */
public class ElasticsearchEngine implements SearchEngine {
    private final ElasticsearchClient client;
    private final QueryExpressionParser parser;
    private final QueryBuilderVisitor visitor;

    /**
     * Creates a new ElasticsearchEngine.
     *
     * @param client The Elasticsearch client
     */
    public ElasticsearchEngine(ElasticsearchClient client) {
        this.client = client;
        this.parser = new QueryExpressionParser();
        this.visitor = new QueryBuilderVisitor();
    }

    @Override
    public <T> Page<T> search(String index, SearchCriteria criteria,
                              Function<Map<String, Object>, T> resultMapper) throws SearchException {
        try {
            // Parse the filter expression if present
            QueryNode filterNode = null;
            if (criteria.getFilter() != null && !criteria.getFilter().trim().isEmpty()) {
                filterNode = parser.parse(criteria.getFilter());
            }

            // Build the search request
            SearchRequest searchRequest = new ElasticsearchQueryBuilder(visitor)
                    .withSearchText(criteria.getSearchText())
                    .withFilter(filterNode)
                    .withSorts(criteria.getSorts())
                    .withPagination(criteria.getPagination())
                    .build();

            // Execute the search
            SearchResponse<Map> response = client.search(searchRequest, Map.class);

            // Process the results
            List<T> items = new ArrayList<>();
            for (Hit<Map> hit : response.hits().hits()) {
                if (hit.source() != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> source = hit.source();
                    T item = resultMapper.apply(source);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }

            // Get the total number of results
            long totalHits = response.hits().total().value();

            return Page.of(items, criteria.getPagination(), totalHits);

        } catch (IOException e) {
            throw new SearchException("Failed to execute search: " + e.getMessage(), e);
        }
    }
}