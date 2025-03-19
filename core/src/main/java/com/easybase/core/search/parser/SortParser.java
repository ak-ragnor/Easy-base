package com.easybase.core.search.parser;

import com.easybase.core.search.model.search.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for sort expressions.
 */
public class SortParser {

    /**
     * Parses a sort expression string into a list of Sort objects.
     *
     * @param sortExpression The sort expression string (e.g., "field1 asc,field2 desc")
     * @return A list of Sort objects
     */
    public List<Sort> parse(String sortExpression) {
        List<Sort> sorts = new ArrayList<>();

        if (sortExpression == null || sortExpression.trim().isEmpty()) {
            return sorts;
        }

        String[] sortClauses = sortExpression.split(",");

        for (String sortClause : sortClauses) {
            String[] parts = sortClause.trim().split("\\s+");
            String fieldName = parts[0].trim();

            boolean reverse = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim());

            sorts.add(new Sort(fieldName, Sort.STRING_TYPE, reverse));
        }

        return sorts;
    }
}