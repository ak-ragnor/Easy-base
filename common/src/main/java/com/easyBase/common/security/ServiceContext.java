package com.easyBase.common.security;

import com.easyBase.common.enums.UserRole;

import java.time.ZonedDateTime;

/**
 * ServiceContext Interface
 * Provides access to current user and site information
 *
 * This interface is in the common module to avoid circular dependencies
 * between security and service modules
 */
public interface ServiceContext {

    /**
     * Get current user ID
     */
    Long getCurrentUserId();

    /**
     * Get current user email
     */
    String getCurrentUserEmail();

    /**
     * Get current site ID
     */
    Long getCurrentSiteId();

    /**
     * Get current site code
     */
    String getCurrentSiteCode();

    /**
     * Get session ID
     */
    String getSessionId();

    /**
     * Check if user is authenticated
     */
    boolean isAuthenticated();

    /**
     * Get current user role
     */
    UserRole getCurrentUserRole();

    /**
     * Get current site role
     */
    UserRole getCurrentSiteRole();

    /**
     * Get session expiry
     */
    ZonedDateTime getSessionExpiry();
}