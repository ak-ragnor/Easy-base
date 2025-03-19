package com.easybase.core.search.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A container for paginated search results.
 *
 * @param <T> The type of the result items
 */
public class Page<T> {
    private final List<T> items;
    private final Pagination pagination;
    private final long totalCount;
    private final Map<String, Map<String, String>> actions;
    private final List<Facet> facets;

    private Page(List<T> items, Pagination pagination, long totalCount,
                 Map<String, Map<String, String>> actions, List<Facet> facets) {
        this.items = items;
        this.pagination = pagination;
        this.totalCount = totalCount;
        this.actions = actions;
        this.facets = facets;
    }

    /**
     * Creates a new Page instance.
     *
     * @param actions Actions available for the page
     * @param facets Facets for the page
     * @param items Items in the page
     * @param pagination Pagination information
     * @param totalCount Total count of all matching items
     * @param <T> Type of the items
     * @return A new Page instance
     */
    public static <T> Page<T> of(
            Map<String, Map<String, String>> actions, List<Facet> facets,
            List<T> items, Pagination pagination, long totalCount) {

        return new Page<>(items, pagination, totalCount, actions, facets);
    }

    /**
     * Creates a new Page instance with no actions or facets.
     *
     * @param items Items in the page
     * @param pagination Pagination information
     * @param totalCount Total count of all matching items
     * @param <T> Type of the items
     * @return A new Page instance
     */
    public static <T> Page<T> of(List<T> items, Pagination pagination, long totalCount) {
        return new Page<>(items, pagination, totalCount,
                Collections.emptyMap(), Collections.emptyList());
    }

    /**
     * Gets the items in this page.
     *
     * @return The items
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * Gets the pagination information.
     *
     * @return The pagination information
     */
    public Pagination getPagination() {
        return pagination;
    }

    /**
     * Gets the total count of all matching items.
     *
     * @return The total count
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * Gets the actions available for this page.
     *
     * @return The actions
     */
    public Map<String, Map<String, String>> getActions() {
        return actions;
    }

    /**
     * Gets the facets for this page.
     *
     * @return The facets
     */
    public List<Facet> getFacets() {
        return facets;
    }

    /**
     * Facet information for search results.
     */
    public static class Facet {
        private final String fieldName;
        private final String label;
        private final Map<String, Long> values;

        public Facet(String fieldName, String label, Map<String, Long> values) {
            this.fieldName = fieldName;
            this.label = label;
            this.values = values;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getLabel() {
            return label;
        }

        public Map<String, Long> getValues() {
            return values;
        }
    }
}