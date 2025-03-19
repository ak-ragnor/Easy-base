package com.easybase.core.search.visitor;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.easybase.core.search.exception.SearchException;
import com.easybase.core.search.model.node.*;
import com.easybase.core.search.model.operator.ComparisonOperator;
import com.easybase.core.search.model.operator.FunctionType;
import com.easybase.core.search.model.operator.LogicalOperator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Visitor that converts query nodes to Elasticsearch Query objects.
 * Uses Strategy pattern for handling different types of range queries.
 */
public class QueryBuilderVisitor implements QueryNodeVisitor<Query> {

    @Override
    public Query visit(ComparisonNode node) {
        String fieldName = node.getProperty().getName();
        Object value = _getNodeValue(node.getValue());

        switch (node.getOperator()) {
            case EQ:
                if (value == null) {
                    return Query.of(q -> q
                            .bool(b -> b
                                    .mustNot(mn -> mn
                                            .exists(e -> e
                                                    .field(fieldName)
                                            )
                                    )
                            )
                    );
                }
                return Query.of(q -> q.term(t -> t.field(fieldName).value(FieldValue.of(value))));

            case NE:
                if (value == null) {
                    return Query.of(q -> q.exists(e -> e.field(fieldName)));
                }
                return Query.of(q -> q
                        .bool(b -> b
                                .mustNot(mn -> mn
                                        .term(t -> t.field(fieldName).value(FieldValue.of(value)))
                                )
                        )
                );

            case GT:
                return _createRangeQuery(fieldName, value, RangeQueryType.GT);

            case GE:
                return _createRangeQuery(fieldName, value, RangeQueryType.GTE);

            case LT:
                return _createRangeQuery(fieldName, value, RangeQueryType.LT);

            case LE:
                return _createRangeQuery(fieldName, value, RangeQueryType.LTE);

            case STARTS_WITH:
                if (value instanceof String) {
                    return Query.of(q -> q.prefix(p -> p.field(fieldName).value((String) value)));
                }
                throw new SearchException("startsWith operator requires a string value");

            default:
                throw new SearchException("Unsupported comparison operator: " + node.getOperator());
        }
    }

    @Override
    public Query visit(LogicalNode node) {
        LogicalOperator operator = node.getOperator();
        List<QueryNode> operands = node.getOperands();

        switch (operator) {
            case AND:
                return Query.of(q -> {
                    BoolQuery.Builder builder = new BoolQuery.Builder();

                    for (QueryNode operand : operands) {
                        builder.must(operand.accept(this));
                    }

                    return q.bool(builder.build());
                });

            case OR:
                return Query.of(q -> {
                    BoolQuery.Builder builder = new BoolQuery.Builder();

                    for (QueryNode operand : operands) {
                        builder.should(operand.accept(this));
                    }

                    return q.bool(builder.build());
                });

            case NOT:
                if (operands.isEmpty()) {
                    throw new SearchException("NOT operator requires an operand");
                }

                return Query.of(q -> q
                        .bool(b -> b
                                .mustNot(operands.get(0).accept(this))
                        )
                );

            default:
                throw new SearchException("Unsupported logical operator: " + operator);
        }
    }

    @Override
    public Query visit(GroupNode node) {
        return node.getChild().accept(this);
    }

    @Override
    public Query visit(FunctionNode node) {
        FunctionType functionType = node.getFunctionType();
        List<QueryNode> arguments = node.getArguments();

        switch (functionType) {
            case CONTAINS:
                if (arguments.size() != 2) {
                    throw new SearchException("contains function requires 2 arguments");
                }

                String fieldName;
                String value;

                // First argument should be field name
                if (arguments.get(0) instanceof PropertyNode) {
                    fieldName = ((PropertyNode) arguments.get(0)).getName();
                } else {
                    throw new SearchException("First argument of contains must be a field name");
                }

                // Second argument should be a string value
                Object secondArg = _getNodeValue(arguments.get(1));
                if (secondArg instanceof String) {
                    value = (String) secondArg;
                } else {
                    throw new SearchException("Second argument of contains must be a string");
                }

                return Query.of(q -> q.wildcard(w -> w.field(fieldName).value("*" + value + "*")));

            default:
                throw new SearchException("Unsupported function: " + functionType);
        }
    }

    @Override
    public Query visit(PropertyNode node) {
        // Property nodes are usually handled in context of other nodes
        // If we get here, it means a property is being used as a value
        return Query.of(q -> q.exists(e -> e.field(node.getName())));
    }

    @Override
    public Query visit(LiteralNode node) {
        // Literal nodes are usually handled in context of other nodes
        // If we get here, it means a literal is being used on its own
        throw new SearchException("Literal node cannot be converted directly to a query");
    }

    /**
     * Gets the value from a node.
     *
     * @param node The node to get value from
     * @return The node's value
     */
    private Object _getNodeValue(QueryNode node) {
        if (node instanceof LiteralNode) {
            return ((LiteralNode) node).getValue();
        } else if (node instanceof PropertyNode) {
            return ((PropertyNode) node).getName();
        } else {
            throw new SearchException("Unsupported value node type: " + node.getClass().getSimpleName());
        }
    }

    /**
     * Enum for range query types.
     */
    private enum RangeQueryType {
        GT, GTE, LT, LTE
    }

    /**
     * Creates a range query based on the value type and comparison operator.
     * Uses Strategy pattern to delegate to the appropriate range query builder.
     *
     * @param fieldName The field name
     * @param value The value
     * @param rangeType The range query type
     * @return The range query
     */
    private Query _createRangeQuery(String fieldName, Object value, RangeQueryType rangeType) {
        if (value == null) {
            throw new SearchException("Range queries cannot be performed on null values");
        }

        // Select the appropriate strategy based on the value type
        if (value instanceof Number) {
            return _createNumberRangeQuery(fieldName, (Number) value, rangeType);
        } else if (value instanceof String) {
            return _createStringRangeQuery(fieldName, (String) value, rangeType);
        } else if (value instanceof Instant || value instanceof LocalDate || value instanceof ZonedDateTime) {
            return _createDateRangeQuery(fieldName, value.toString(), rangeType);
        } else {
            throw new SearchException("Unsupported value type for range query: " + value.getClass().getName());
        }
    }

    /**
     * Creates a number range query.
     */
    private Query _createNumberRangeQuery(String fieldName, Number value, RangeQueryType rangeType) {
        return Query.of(q -> {
            switch (rangeType) {
                case GT:
                    return q.range(r -> r
                            .number(nb -> nb
                                    .field(fieldName)
                                    .gt(value.doubleValue()))
                            );
                case GTE:
                    return q.range(r -> r
                            .number(nb -> nb
                                    .field(fieldName)
                                    .gte(value.doubleValue()))
                            );
                case LT:
                    return q.range(r -> r
                            .number(nb -> nb
                                    .field(fieldName)
                                    .lt(value.doubleValue()))
                            );
                case LTE:
                    return q.range(r -> r
                            .number(nb -> nb
                                    .field(fieldName)
                                    .lte(value.doubleValue()))
                            );
                default:
                    throw new SearchException("Unexpected range type: " + rangeType);
            }
        });
    }

    /**
     * Creates a string range query.
     */
    private Query _createStringRangeQuery(String fieldName, String value, RangeQueryType rangeType) {
        return Query.of(q -> {
            switch (rangeType) {
                case GT:
                    return q.range(r -> r
                            .term(nb -> nb
                                    .field(fieldName)
                                    .gt(value))
                    );
                case GTE:
                    return q.range(r -> r
                            .term(nb -> nb
                                    .field(fieldName)
                                    .gte(value))
                    );
                case LT:
                    return q.range(r -> r
                            .term(nb -> nb
                                    .field(fieldName)
                                    .lt(value))
                    );
                case LTE:
                    return q.range(r -> r
                            .term(nb -> nb
                                    .field(fieldName)
                                    .lte(value))
                    );
                default:
                    throw new SearchException("Unexpected range type: " + rangeType);
            }
        });
    }

    /**
     * Creates a date range query.
     */
    private Query _createDateRangeQuery(String fieldName, String value, RangeQueryType rangeType) {
        return Query.of(q -> {
            switch (rangeType) {
                case GT:
                    return q.range(r -> r
                            .date(nb -> nb
                                    .field(fieldName)
                                    .gt(value))
                    );
                case GTE:
                    return q.range(r -> r
                            .date(nb -> nb
                                    .field(fieldName)
                                    .gte(value))
                    );
                case LT:
                    return q.range(r -> r
                            .date(nb -> nb
                                    .field(fieldName)
                                    .lt(value))
                    );
                case LTE:
                    return q.range(r -> r
                            .date(nb -> nb
                                    .field(fieldName)
                                    .lte(value))
                    );
                default:
                    throw new SearchException("Unexpected range type: " + rangeType);
            }
        });
    }
}