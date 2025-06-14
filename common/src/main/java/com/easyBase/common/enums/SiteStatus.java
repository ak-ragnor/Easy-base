package com.easyBase.common.enums;

/**
 * Site Status Enumeration
 *
 * Defines the possible status values for a Site in the system.
 * Used for site lifecycle management and access control.
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
public enum SiteStatus {

    /**
     * Site is active and fully operational
     * - Users can access the site
     * - All features are available
     * - Data operations are permitted
     */
    ACTIVE("Active", "Site is operational and available"),

    /**
     * Site is temporarily inactive
     * - Users cannot access the site
     * - Site data is preserved
     * - Can be reactivated
     */
    INACTIVE("Inactive", "Site is temporarily disabled"),

    /**
     * Site is suspended due to policy violations or administrative reasons
     * - All access is blocked
     * - Requires administrative intervention to reactivate
     * - Site data is preserved
     */
    SUSPENDED("Suspended", "Site is suspended and requires admin intervention"),

    /**
     * Site is under maintenance
     * - Limited or no access during maintenance
     * - Users may see maintenance messages
     * - Temporary status during updates
     */
    MAINTENANCE("Maintenance", "Site is under maintenance"),

    /**
     * Site is being archived and will be deleted
     * - Read-only access for data export
     * - No new operations allowed
     * - Precedes permanent deletion
     */
    ARCHIVED("Archived", "Site is archived and read-only"),

    /**
     * Site configuration is being set up
     * - Site is not yet ready for production use
     * - Only administrators can access
     * - Used during initial setup phase
     */
    SETUP("Setup", "Site is being configured");

    private final String displayName;
    private final String description;

    /**
     * Constructor for SiteStatus enum
     *
     * @param displayName Human-readable name for the status
     * @param description Detailed description of what this status means
     */
    SiteStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Get the human-readable display name
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the detailed description of this status
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Check if the site status allows user access
     *
     * @return true if users can access the site with this status
     */
    public boolean isAccessible() {
        return this == ACTIVE || this == MAINTENANCE;
    }

    /**
     * Check if the site status allows data modifications
     *
     * @return true if data can be modified with this status
     */
    public boolean isModifiable() {
        return this == ACTIVE;
    }

    /**
     * Check if the status represents an operational state
     *
     * @return true if the site is in an operational state
     */
    public boolean isOperational() {
        return this == ACTIVE || this == MAINTENANCE;
    }

    /**
     * Get all statuses that allow user access
     *
     * @return array of accessible statuses
     */
    public static SiteStatus[] getAccessibleStatuses() {
        return new SiteStatus[]{ACTIVE, MAINTENANCE};
    }

    /**
     * Get all statuses that allow data modification
     *
     * @return array of modifiable statuses
     */
    public static SiteStatus[] getModifiableStatuses() {
        return new SiteStatus[]{ACTIVE};
    }

    /**
     * Convert string value to SiteStatus enum
     * Case-insensitive conversion with fallback
     *
     * @param value String value to convert
     * @return SiteStatus enum or null if not found
     */
    public static SiteStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return SiteStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try matching by display name
            for (SiteStatus status : values()) {
                if (status.getDisplayName().equalsIgnoreCase(value.trim())) {
                    return status;
                }
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}