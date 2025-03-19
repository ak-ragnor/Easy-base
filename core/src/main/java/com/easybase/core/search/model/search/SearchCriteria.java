package com.easybase.core.search.model.search;

import com.easybase.core.search.api.Pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for all search parameters: search text, filter, sort, and pagination.
 */
public class SearchCriteria {
    private final String searchText;
    private final String filter;
    private final List<Sort> sorts;
    private final Pagination pagination;

    private SearchCriteria(Builder builder) {
        this.searchText = builder.searchText;
        this.filter = builder.filter;
        this.sorts = Collections.unmodifiableList(new ArrayList<>(builder.sorts));
        this.pagination = builder.pagination;
    }

    /**
     * Gets the search text for keyword search.
     *
     * @return The search text, or null if not specified
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * Gets the filter expression.
     *
     * @return The filter expression, or null if not specified
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Gets the sort criteria.
     *
     * @return The sort criteria
     */
    public List<Sort> getSorts() {
        return sorts;
    }

    /**
     * Gets the pagination parameters.
     *
     * @return The pagination parameters
     */
    public Pagination getPagination() {
        return pagination;
    }

    /**
     * Creates a new Builder instance.
     *
     * @return A new Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for SearchCriteria.
     */
    public static class Builder {
        private String searchText;
        private String filter;
        private List<Sort> sorts = new ArrayList<>();
        private Pagination pagination = Pagination.ofDefault();

        /**
         * Sets the search text for keyword search.
         *
         * @param searchText The search text
         * @return This builder
         */
        public Builder searchText(String searchText) {
            this.searchText = searchText;
            return this;
        }

        /**
         * Sets the filter expression.
         *
         * @param filter The filter expression
         * @return This builder
         */
        public Builder filter(String filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Adds a sort criterion.
         *
         * @param sort The sort criterion
         * @return This builder
         */
        public Builder addSort(Sort sort) {
            this.sorts.add(sort);
            return this;
        }

        /**
         * Sets all sort criteria.
         *
         * @param sorts The sort criteria
         * @return This builder
         */
        public Builder sorts(List<Sort> sorts) {
            this.sorts = new ArrayList<>(sorts);
            return this;
        }

        /**
         * Sets the pagination parameters.
         *
         * @param pagination The pagination parameters
         * @return This builder
         */
        public Builder pagination(Pagination pagination) {
            this.pagination = pagination;
            return this;
        }

        /**
         * Builds a new SearchCriteria instance.
         *
         * @return A new SearchCriteria
         */
        public SearchCriteria build() {
            return new SearchCriteria(this);
        }
    }
}