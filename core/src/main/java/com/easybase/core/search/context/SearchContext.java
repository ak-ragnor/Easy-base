package com.easybase.core.search.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for search operations.
 */
public class SearchContext {
    private final Map<String, Object> attributes = new HashMap<>();
    private boolean checkPermissions = true;

    /**
     * Gets an attribute from this context.
     *
     * @param name The attribute name
     * @return The attribute value
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Sets an attribute in this context.
     *
     * @param name The attribute name
     * @param value The attribute value
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * Checks if permission checking is enabled.
     *
     * @return True if permission checking is enabled
     */
    public boolean isCheckPermissions() {
        return checkPermissions;
    }

    /**
     * Sets whether permission checking is enabled.
     *
     * @param checkPermissions Whether to check permissions
     */
    public void setCheckPermissions(boolean checkPermissions) {
        this.checkPermissions = checkPermissions;
    }
}