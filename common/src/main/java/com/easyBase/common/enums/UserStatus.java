package com.easyBase.common.enums;

/**
 * Enterprise User Status Enumeration
 *
 * Defines the various states a user account can be in.
 * This enum provides type safety and ensures consistent status management.
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
public enum UserStatus {

    /**
     * User account is active and can access the system
     * - User can log in and perform authorized operations
     * - All system features are available based on role
     * - This is the normal operational state
     */
    ACTIVE("Active", "User account is active and operational", true, true),

    /**
     * User account is temporarily inactive
     * - User cannot log in or access the system
     * - Account can be reactivated by administrators
     * - Data is preserved for future reactivation
     */
    INACTIVE("Inactive", "User account is temporarily disabled", false, true),

    /**
     * User account is locked due to security reasons
     * - Account locked due to multiple failed login attempts
     * - Account locked due to suspicious activity
     * - Requires administrative intervention to unlock
     */
    LOCKED("Locked", "User account is locked for security reasons", false, true),

    /**
     * User account is suspended pending investigation
     * - Temporary suspension for policy violations
     * - Account under review by administrators
     * - May be reactivated or permanently disabled
     */
    SUSPENDED("Suspended", "User account is suspended pending review", false, true),

    /**
     * User account is permanently disabled
     * - Account cannot be reactivated through normal means
     * - Used for terminated employees or permanent bans
     * - Data may be archived or anonymized
     */
    DISABLED("Disabled", "User account is permanently disabled", false, false);

    // ===== ENUM PROPERTIES =====

    private final String displayName;
    private final String description;
    private final boolean canLogin;
    private final boolean canReactivate;

    // ===== CONSTRUCTOR =====

    /**
     * Constructor for UserStatus enum
     *
     * @param displayName    human-readable name for UI display
     * @param description    detailed description of the status
     * @param canLogin       whether user can log in with this status
     * @param canReactivate  whether status can be changed to ACTIVE
     */
    UserStatus(String displayName, String description, boolean canLogin, boolean canReactivate) {
        this.displayName = displayName;
        this.description = description;
        this.canLogin = canLogin;
        this.canReactivate = canReactivate;
    }

    // ===== GETTERS =====

    /**
     * Gets the display name for UI presentation
     *
     * @return human-readable status name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the detailed description of the status
     *
     * @return status description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if users with this status can log in
     *
     * @return true if login is allowed
     */
    public boolean canLogin() {
        return canLogin;
    }

    /**
     * Checks if this status can be changed to ACTIVE
     *
     * @return true if reactivation is possible
     */
    public boolean canReactivate() {
        return canReactivate;
    }

    // ===== BUSINESS METHODS =====

    /**
     * Checks if this status allows system access
     * Only ACTIVE users can fully access the system
     *
     * @return true if full system access is allowed
     */
    public boolean allowsSystemAccess() {
        return this == ACTIVE;
    }

    /**
     * Checks if this status indicates the account is operational
     *
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Checks if this status indicates the account is non-operational
     *
     * @return true if status is not ACTIVE
     */
    public boolean isInactive() {
        return this != ACTIVE;
    }

    /**
     * Checks if this status indicates a security-related restriction
     *
     * @return true if status is LOCKED or SUSPENDED
     */
    public boolean isSecurityRestricted() {
        return this == LOCKED || this == SUSPENDED;
    }

    /**
     * Checks if this status indicates a permanent restriction
     *
     * @return true if status is DISABLED
     */
    public boolean isPermanentlyRestricted() {
        return this == DISABLED;
    }

    /**
     * Checks if transition to the target status is allowed
     *
     * @param targetStatus the desired new status
     * @return true if transition is allowed
     */
    public boolean canTransitionTo(UserStatus targetStatus) {
        if (targetStatus == null) {
            return false;
        }

        // Same status - no change needed
        if (this == targetStatus) {
            return true;
        }

        // From DISABLED - only admins can reactivate in special cases
        if (this == DISABLED) {
            return false; // Generally not allowed
        }

        // To DISABLED - always allowed (administrative action)
        if (targetStatus == DISABLED) {
            return true;
        }

        // To ACTIVE - check if reactivation is allowed
        if (targetStatus == ACTIVE) {
            return this.canReactivate();
        }

        // Other transitions are generally allowed
        return true;
    }

    /**
     * Gets the next logical status for workflow transitions
     *
     * @return suggested next status or null if no logical transition
     */
    public UserStatus getNextLogicalStatus() {
        switch (this) {
            case INACTIVE:
            case LOCKED:
            case SUSPENDED:
                return ACTIVE; // Reactivation
            case ACTIVE:
                return INACTIVE; // Deactivation
            case DISABLED:
                throw new IllegalStateException("Cannot transition from DISABLED status");
            default:
                throw new IllegalStateException("Unknown user status: " + this);
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Converts string to UserStatus enum safely
     *
     * @param statusString the string representation of the status
     * @return UserStatus enum or ACTIVE as default
     */
    public static UserStatus fromString(String statusString) {
        if (statusString == null || statusString.trim().isEmpty()) {
            return ACTIVE; // Default status
        }

        try {
            return UserStatus.valueOf(statusString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE; // Default status for invalid input
        }
    }

    /**
     * Gets all available statuses as an array
     *
     * @return array of all UserStatus values
     */
    public static UserStatus[] getAllStatuses() {
        return UserStatus.values();
    }

    /**
     * Gets all active/operational statuses
     *
     * @return array containing only ACTIVE status
     */
    public static UserStatus[] getOperationalStatuses() {
        return new UserStatus[]{ACTIVE};
    }

    /**
     * Gets all inactive/restricted statuses
     *
     * @return array of non-active statuses
     */
    public static UserStatus[] getRestrictedStatuses() {
        return new UserStatus[]{INACTIVE, LOCKED, SUSPENDED, DISABLED};
    }

    /**
     * String representation for logging and debugging
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("UserStatus{name=%s, displayName='%s', canLogin=%s, canReactivate=%s}",
                name(), displayName, canLogin, canReactivate);
    }
}