package com.easybase.core.search.model.search;

/**
 * Sort criterion for search operations.
 */
public class Sort {
    public static final int LONG_TYPE = 0;
    public static final int STRING_TYPE = 1;
    public static final int DOUBLE_TYPE = 2;
    public static final int FLOAT_TYPE = 3;

    private final String fieldName;
    private final int type;
    private final boolean reverse;

    /**
     * Creates a new Sort instance.
     *
     * @param fieldName The field to sort by
     * @param type The type of the field
     * @param reverse Whether to reverse the sort order (descending)
     */
    public Sort(String fieldName, int type, boolean reverse) {
        this.fieldName = fieldName;
        this.type = type;
        this.reverse = reverse;
    }

    /**
     * Gets the field name to sort by.
     *
     * @return The field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Gets the type of the field.
     *
     * @return The field type
     */
    public int getType() {
        return type;
    }

    /**
     * Checks if the sort order is reversed (descending).
     *
     * @return True if the sort order is reversed, false otherwise
     */
    public boolean isReverse() {
        return reverse;
    }

    /**
     * Creates an ascending sort by the given field name.
     *
     * @param fieldName The field name
     * @return The sort criterion
     */
    public static Sort asc(String fieldName) {
        return new Sort(fieldName, STRING_TYPE, false);
    }

    /**
     * Creates a descending sort by the given field name.
     *
     * @param fieldName The field name
     * @return The sort criterion
     */
    public static Sort desc(String fieldName) {
        return new Sort(fieldName, STRING_TYPE, true);
    }
}