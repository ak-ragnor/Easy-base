package com.easy.base.factory.builder;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.regex.Pattern;


//not implemeted need to implement in search
public class QueryBuilder {
    private static final Pattern COMPARISON_PATTERN = Pattern.compile("([\\w.]+)\\s*([!=><~?]+)\\s*'([^']*)'");
    private static final Pattern AND_SPLIT_PATTERN = Pattern.compile("\\s*&&\\s*");
    private static final Pattern OR_SPLIT_PATTERN = Pattern.compile("\\s*\\|\\|\\s*");

    private static final Map<String, BiFunction<String, String, Bson>> OPERATOR_MAP = new ConcurrentHashMap<>();

    static {
        OPERATOR_MAP.put("=", Filters::eq);
        OPERATOR_MAP.put("!=", Filters::ne);
        OPERATOR_MAP.put(">", Filters::gt);
        OPERATOR_MAP.put(">=", Filters::gte);
        OPERATOR_MAP.put("<", Filters::lt);
        OPERATOR_MAP.put("<=", Filters::lte);
        OPERATOR_MAP.put("~", (field, value) -> Filters.regex(field, ".*" + Pattern.quote(value) + ".*", "i"));
        OPERATOR_MAP.put("!~", (field, value) -> Filters.not(Filters.regex(field, ".*" + Pattern.quote(value) + ".*", "i")));
        OPERATOR_MAP.put("?=", (field, value) -> Filters.in(field, _parseList(value)));
        OPERATOR_MAP.put("?!=", (field, value) -> Filters.nin(field, _parseList(value)));
    }

    public Bson buildQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }

        return _parseExpression(query.trim());
    }

    private Bson _parseExpression(String expression) {
        try {
            if (expression.contains("&&") || expression.contains("||")) {
                return _parseLogicalExpression(expression);
            }

            return _parseComparisonExpression(expression);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse expression: " + expression, e);
        }
    }

    private Bson _parseLogicalExpression(String expression) {
        String[] andParts = AND_SPLIT_PATTERN.split(expression);

        List<Bson> andFilters = Arrays.stream(andParts)
                .map(this::_parseOrExpression)
                .toList();

        return andFilters.size() > 1 ? Filters.and(andFilters) : andFilters.get(0);
    }

    private Bson _parseOrExpression(String expression) {
        String[] orParts = OR_SPLIT_PATTERN.split(expression);

        List<Bson> orFilters = Arrays.stream(orParts)
                .map(part -> _parseComparisonExpression(part.trim()))
                .toList();

        return orFilters.size() > 1 ? Filters.or(orFilters) : orFilters.get(0);
    }

    private Bson _parseComparisonExpression(String expression) {
        var matcher = COMPARISON_PATTERN.matcher(expression);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid comparison expression: " + expression);
        }

        String field = matcher.group(1);
        String operator = matcher.group(2);
        String value = matcher.group(3);

        BiFunction<String, String, Bson> filterFunction = OPERATOR_MAP.get(operator);
        if (filterFunction == null) {
            throw new IllegalArgumentException("Unknown operator: " + operator);
        }

        return filterFunction.apply(field, value);
    }

    private static List<String> _parseList(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
