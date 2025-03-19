package com.easybase.core.search.builder;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.easybase.core.search.api.Pagination;
import com.easybase.core.search.model.node.QueryNode;
import com.easybase.core.search.model.search.Sort;
import com.easybase.core.search.visitor.QueryNodeVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Builder for Elasticsearch search requests.
 * This builds SearchRequest objects for Elasticsearch.
 */
public class ElasticsearchQueryBuilder implements com.easybase.core.search.builder.QueryBuilder<SearchRequest> {
    private String searchText;
    private QueryNode filterNode;
    private List<Sort> sorts = new ArrayList<>();
    private Pagination pagination;
    private final QueryNodeVisitor<Query> queryBuilderVisitor;

    /**
     * Creates a new ElasticsearchQueryBuilder.
     *
     * @param queryBuilderVisitor The visitor for building Elasticsearch queries from AST nodes
     */
    public ElasticsearchQueryBuilder(QueryNodeVisitor<Query> queryBuilderVisitor) {
        this.queryBuilderVisitor = queryBuilderVisitor;
    }

    @Override
    public com.easybase.core.search.builder.QueryBuilder<SearchRequest> withSearchText(String searchText) {
        this.searchText = searchText;
        return this;
    }

    @Override
    public com.easybase.core.search.builder.QueryBuilder<SearchRequest> withFilter(QueryNode filterNode) {
        this.filterNode = filterNode;
        return this;
    }

    @Override
    public com.easybase.core.search.builder.QueryBuilder<SearchRequest> withSorts(List<Sort> sorts) {
        this.sorts = sorts != null ? new ArrayList<>(sorts) : new ArrayList<>();
        return this;
    }

    @Override
    public com.easybase.core.search.builder.QueryBuilder<SearchRequest> withPagination(Pagination pagination) {
        this.pagination = pagination;
        return this;
    }

    @Override
    public SearchRequest build() {
        // Build the query
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // Add search text query if provided
        if (searchText != null && !searchText.trim().isEmpty()) {
            boolQueryBuilder.must(
                    Query.of(q -> q
                            .multiMatch(mm -> mm
                                    .query(searchText)
                                    .type(TextQueryType.BestFields)
                                    .fuzziness("AUTO")
                                    .fields("*")
                            )
                    )
            );
        }

        // Add filter if provided
        if (filterNode != null) {
            Query filterQuery = filterNode.accept(queryBuilderVisitor);
            boolQueryBuilder.filter(filterQuery);
        }

        // Build the search request
        Function<SearchRequest.Builder, SearchRequest.Builder> builderFunction = b -> b
                .query(q -> q.bool(boolQueryBuilder.build()));

        // Add sort if provided
        if (sorts != null && !sorts.isEmpty()) {
            for (Sort sort : sorts) {
                SortOrder order = sort.isReverse() ? SortOrder.Desc : SortOrder.Asc;
                builderFunction = builderFunction.andThen(b ->
                        b.sort(s -> s.field(f -> f.field(sort.getFieldName()).order(order)))
                );
            }
        }

        // Add pagination if provided
        if (pagination != null) {
            builderFunction = builderFunction.andThen(b ->
                    b.from(pagination.getStartPosition()).size(pagination.getSize())
            );
        }

        return builderFunction.apply(new SearchRequest.Builder()).build();
    }
}