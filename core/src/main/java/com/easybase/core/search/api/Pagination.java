package com.easybase.core.search.api;

/**
 * Pagination parameters for search operations.
 */
public class Pagination {
    private final int page;
    private final int size;

    /**
     * Creates a new Pagination instance.
     *
     * @param page The page number (0-based)
     * @param size The page size
     */
    public Pagination(int page, int size) {
        this.page = Math.max(0, page);
        this.size = Math.max(1, size);
    }

    /**
     * Gets the current page number (0-based).
     *
     * @return The page number
     */
    public int getPage() {
        return page;
    }

    /**
     * Gets the page size.
     *
     * @return The page size
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets the start position (offset) for this pagination.
     *
     * @return The start position
     */
    public int getStartPosition() {
        return page * size;
    }

    /**
     * Gets the end position for this pagination.
     *
     * @return The end position
     */
    public int getEndPosition() {
        return getStartPosition() + size;
    }

    /**
     * Creates a default pagination with page 0 and size 10.
     *
     * @return The default pagination
     */
    public static Pagination ofDefault() {
        return new Pagination(0, 10);
    }

    /**
     * Creates a pagination with the given page and size.
     *
     * @param page The page number (0-based)
     * @param size The page size
     * @return The pagination
     */
    public static Pagination of(int page, int size) {
        return new Pagination(page, size);
    }
}