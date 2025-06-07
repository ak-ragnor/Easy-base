package com.easyBase.common.enums;

/**
 * Enterprise User Role Enumeration
 *
 * Defines the various roles a user can have in the system.
 * This enum provides type safety and ensures consistent role management.
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
public enum UserRole {

    /**
     * System Administrator with full access to all system features
     * - Can manage all users and system settings
     * - Has access to admin panels and system monitoring
     * - Can perform all CRUD operations on all entities
     */
    ADMIN("Admin", "System Administrator with full access", 1000),

    /**
     * Manager with elevated privileges
     * - Can manage users within their scope
     * - Has access to reports and analytics
     * - Can perform most operations with some restrictions
     */
    MANAGER("Manager", "Manager with elevated privileges", 500),

    /**
     * Regular user with standard access
     * - Can access standard application features
     * - Limited to operations on their own data
     * - Cannot perform administrative functions
     */
    USER("User", "Regular user with standard access", 100);

    // ===== ENUM PROPERTIES =====

    private final String displayName;
    private final String description;
    private final int priorityLevel;

    // ===== CONSTRUCTOR =====

    /**
     * Constructor for UserRole enum
     *
     * @param displayName   human-readable name for UI display
     * @param description   detailed description of the role
     * @param priorityLevel numeric priority level (higher = more privileges)
     */
    UserRole(String displayName, String description, int priorityLevel) {
        this.displayName = displayName;
        this.description = description;
        this.priorityLevel = priorityLevel;
    }

    // ===== GETTERS =====

    /**
     * Gets the display name for UI presentation
     *
     * @return human-readable role name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the detailed description of the role
     *
     * @return role description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the priority level of the role
     * Higher values indicate more privileges
     *
     * @return numeric priority level
     */
    public int getPriorityLevel() {
        return priorityLevel;
    }

    // ===== BUSINESS METHODS =====

    /**
     * Checks if this role has higher or equal privilege than another role
     *
     * @param otherRole the role to compare against
     * @return true if this role has higher or equal privilege
     */
    public boolean hasHigherOrEqualPrivilegeThan(UserRole otherRole) {
        return this.priorityLevel >= otherRole.priorityLevel;
    }

    /**
     * Checks if this role has lower privilege than another role
     *
     * @param otherRole the role to compare against
     * @return true if this role has lower privilege
     */
    public boolean hasLowerPrivilegeThan(UserRole otherRole) {
        return this.priorityLevel < otherRole.priorityLevel;
    }

    /**
     * Checks if this role can manage (create/update/delete) users with the specified role
     *
     * @param targetRole the role of the user to be managed
     * @return true if this role can manage users with the target role
     */
    public boolean canManage(UserRole targetRole) {
        // ADMIN can manage everyone
        if (this == ADMIN) {
            return true;
        }

        // MANAGER can manage USER but not other MANAGERs or ADMINs
        if (this == MANAGER) {
            return targetRole == USER;
        }

        // USER cannot manage anyone
        return false;
    }

    /**
     * Checks if this is an administrative role (ADMIN or MANAGER)
     *
     * @return true if this is an administrative role
     */
    public boolean isAdministrative() {
        return this == ADMIN || this == MANAGER;
    }

    /**
     * Checks if this is the highest privilege role
     *
     * @return true if this is ADMIN role
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Checks if this is a regular user role
     *
     * @return true if this is USER role
     */
    public boolean isUser() {
        return this == USER;
    }

    // ===== UTILITY METHODS =====

    /**
     * Converts string to UserRole enum safely
     *
     * @param roleString the string representation of the role
     * @return UserRole enum or USER as default
     */
    public static UserRole fromString(String roleString) {
        if (roleString == null || roleString.trim().isEmpty()) {
            return USER; // Default role
        }

        try {
            return UserRole.valueOf(roleString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return USER; // Default role for invalid input
        }
    }

    /**
     * Gets all available roles as an array
     *
     * @return array of all UserRole values
     */
    public static UserRole[] getAllRoles() {
        return UserRole.values();
    }

    /**
     * Gets all administrative roles
     *
     * @return array of administrative roles
     */
    public static UserRole[] getAdministrativeRoles() {
        return new UserRole[]{ADMIN, MANAGER};
    }

    /**
     * String representation for logging and debugging
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("UserRole{name=%s, displayName='%s', priority=%d}",
                name(), displayName, priorityLevel);
    }
}