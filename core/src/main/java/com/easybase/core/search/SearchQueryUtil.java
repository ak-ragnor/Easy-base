package com.easybase.core.search;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.json.JsonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for building Elasticsearch queries.
 */
@Component
public class SearchQueryUtil {

    private static final Logger logger = LoggerFactory.getLogger(SearchQueryUtil.class);

    // Pattern for OData filter expressions
    private static final Pattern COMPARISON_PATTERN = Pattern.compile(
            "(\\w+)\\s+(eq|ne|gt|ge|lt|le|contains|startswith|endswith)\\s+(['\"]?[^'\"]+['\"]?)"
    );
    private static final Pattern LOGICAL_PATTERN = Pattern.compile("(.*?)\\s+(and|or)\\s+(.*)");

    /**
     * Builds a search query.
     *
     * @param searchText the search text (for full-text search)
     * @param filter the filter expression (OData-style)
     * @return the query
     */
    public Query buildQuery(String searchText, String filter) {
        BoolQuery.Builder queryBuilder = new BoolQuery.Builder();

        // Add full-text search if provided
        if (searchText != null && !searchText.isEmpty()) {
            queryBuilder.must(buildSearchQuery(searchText));
        }

        // Add filter if provided
        if (filter != null && !filter.isEmpty()) {
            queryBuilder.filter(parseFilterExpression(filter));
        }

        return Query.of(q -> q.bool(queryBuilder.build()));
    }

    /**
     * Builds a multi-match query for full-text search.
     *
     * @param searchText the search text
     * @return the query
     */
    private Query buildSearchQuery(String searchText) {
        return Query.of(q -> q
                .multiMatch(mm -> mm
                        .query(searchText)
                        .type(MultiMatchQueryType.BestFields)
                        .fuzziness("AUTO")
                )
        );
    }

    /**
     * Parses an OData-style filter expression into an Elasticsearch query.
     *
     * @param filter the filter expression
     * @return the query
     */
    public Query parseFilterExpression(String filter) {
        // Check for logical operators first
        Matcher logicalMatcher = LOGICAL_PATTERN.matcher(filter);
        if (logicalMatcher.matches()) {
            String leftExpr = logicalMatcher.group(1).trim();
            String operator = logicalMatcher.group(2).trim().toLowerCase();
            String rightExpr = logicalMatcher.group(3).trim();

            Query leftQuery = parseFilterExpression(leftExpr);
            Query rightQuery = parseFilterExpression(rightExpr);

            if ("and".equals(operator)) {
                return Query.of(q -> q
                        .bool(b -> b
                                .must(leftQuery)
                                .must(rightQuery)
                        )
                );
            } else if ("or".equals(operator)) {
                return Query.of(q -> q
                        .bool(b -> b
                                .should(leftQuery)
                                .should(rightQuery)
                                .minimumShouldMatch("1")
                        )
                );
            }
        }

        // Check for comparison operators
        Matcher comparisonMatcher = COMPARISON_PATTERN.matcher(filter);
        if (comparisonMatcher.matches()) {
            String field = comparisonMatcher.group(1).trim();
            String operator = comparisonMatcher.group(2).trim().toLowerCase();
            String value = comparisonMatcher.group(3).trim();

            // Remove quotes if present
            if ((value.startsWith("'") && value.endsWith("'")) ||
                    (value.startsWith("\"") && value.endsWith("\""))) {
                value = value.substring(1, value.length() - 1);
            }

            return buildComparisonQuery(field, operator, value);
        }

        // If we can't parse it, return a match_all query
        logger.warn("Could not parse filter expression: {}", filter);
        return Query.of(q -> q.matchAll(m -> m));
    }

    /**
     * Builds a comparison query.
     *
     * @param field the field name
     * @param operator the operator (eq, ne, gt, etc.)
     * @param value the value
     * @return the query
     */
    private Query buildComparisonQuery(String field, String operator, String value) {
        // Handle special value null
        if ("null".equalsIgnoreCase(value)) {
            if ("eq".equals(operator)) {
                return Query.of(q -> q.bool(b -> b.mustNot(mn -> mn.exists(e -> e.field(field)))));
            } else if ("ne".equals(operator)) {
                return Query.of(q -> q.exists(e -> e.field(field)));
            }
        }

        // Handle field paths (e.g., 'department.name')
        String[] fieldPath = field.split("\\.");
        if (fieldPath.length > 1) {
            return buildNestedQuery(fieldPath, operator, value);
        }

        // Handle regular comparison operators
        switch (operator) {
            case "eq":
                return Query.of(q -> q.term(t -> t.field(field).value(value)));

            case "ne":
                return Query.of(q -> q.bool(b -> b.mustNot(mn -> mn.term(t -> t.field(field).value(value)))));

            case "gt":
                return Query.of(q -> q.range(r -> r.field(field).gt(JsonData.of(value))));

            case "ge":
                return Query.of(q -> q.range(r -> r.field(field).gte(JsonData.of(value))));

            case "lt":
                return Query.of(q -> q.range(r -> r.field(field).lt(JsonData.of(value))));

            case "le":
                return Query.of(q -> q.range(r -> r.field(field).lte(JsonData.of(value))));

            case "contains":
                return Query.of(q -> q.wildcard(w -> w.field(field).value("*" + value + "*")));

            case "startswith":
                return Query.of(q -> q.prefix(p -> p.field(field).value(value)));

            case "endswith":
                return Query.of(q -> q.wildcard(w -> w.field(field).value("*" + value)));

            default:
                logger.warn("Unsupported operator: {}", operator);
                return Query.of(q -> q.matchAll(m -> m));
        }
    }

    /**
     * Builds a nested query for field paths.
     *
     * @param fieldPath the field path
     * @param operator the operator
     * @param value the value
     * @return the query
     */
    private Query buildNestedQuery(String[] fieldPath, String operator, String value) {
        String rootField = fieldPath[0];
        String nestedField = String.join(".", fieldPath);

        // Create the nested filter
        Query nestedFilter = buildComparisonQuery(nestedField, operator, value);

        return Query.of(q -> q.nested(n -> n
                .path(rootField)
                .query(nestedFilter)
                .scoreMode(NestedScoreMode.None)
        ));
    }

    /**
     * Parses a sort expression into field sorts.
     *
     * @param sortExpr the sort expression (e.g., "name asc, age desc")
     * @return the list of field sorts
     */
    public List<FieldSort> parseSortExpression(String sortExpr) {
        List<FieldSort> sorts = new ArrayList<>();

        if (sortExpr == null || sortExpr.isEmpty()) {
            return sorts;
        }

        // Split by commas
        String[] sortClauses = sortExpr.split(",");

        for (String sortClause : sortClauses) {
            sortClause = sortClause.trim();

            // Split by whitespace
            String[] parts = sortClause.split("\\s+");
            String field = parts[0].trim();
            SortOrder order = SortOrder.Asc;

            if (parts.length > 1) {
                String orderStr = parts[1].trim().toLowerCase();
                if ("desc".equals(orderStr)) {
                    order = SortOrder.Desc;
                }
            }

            // Handle nested fields
            if (field.contains(".")) {
                String[] fieldPath = field.split("\\.");
                String path = fieldPath[0];

                sorts.add(FieldSort.of(fs -> fs
                        .field(field)
                        .order(order)
                        .nested(n -> n.path(path))
                ));
            } else {
                sorts.add(FieldSort.of(fs -> fs
                        .field(field)
                        .order(order)
                ));
            }
        }

        return sorts;
    }
}