package com.easyBase.security.context;

import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
import com.easyBase.domain.entity.user.User;
import com.easyBase.common.enums.UserRole;

import java.time.ZonedDateTime;

/**
 * Simplified Service Context - No validation needed since filter already validated everything
 */
public interface ServiceContext {

    /**
     * Get the currently authenticated user (guaranteed to be valid)
     */
    User getCurrentUser();

    /**
     * Get the current site context (guaranteed to be valid and active)
     */
    Site getCurrentSite();

    /**
     * Get the user-site association (guaranteed to be valid and active)
     */
    UserSite getUserSiteAssociation();

    /**
     * Get current session ID
     */
    String getSessionId();

    /**
     * Check current user exist
     */
    boolean hasCurrentUser();

    /**
     * Check current site exist
     */
    boolean hasCurrentSite();

    /**
     * Check if user is authenticated (if request reached service, this is always true)
     */
    boolean isAuthenticated();

    /**
     * Get user's global role
     */
    UserRole getCurrentUserRole();

    /**
     * Get user's role in current site
     */
    UserRole getCurrentSiteRole();

    /**
     * Get session expiry time
     */
    ZonedDateTime getSessionExpiry();

    /**
     * Static access methods for convenience - NO VALIDATION NEEDED
     */
    final class Current {

        public static User user() {
            ServiceContext context = ServiceContextHolder.getContext();
            return context.getCurrentUser();
        }

        public static Site site() {
            ServiceContext context = ServiceContextHolder.getContext();
            return context.getCurrentSite();
        }

        public static UserSite userSite() {
            ServiceContext context = ServiceContextHolder.getContext();
            return context.getUserSiteAssociation();
        }

        public static String sessionId() {
            ServiceContext context = ServiceContextHolder.getContext();
            return context.getSessionId();
        }

        public static UserRole userRole() {
            ServiceContext context = ServiceContextHolder.getContext();
            return context.getCurrentUserRole();
        }

        public static UserRole siteRole() {
            ServiceContext context = ServiceContextHolder.getContext();
            return context.getCurrentSiteRole();
        }

        public static boolean isAuthenticated() {
            ServiceContext context = ServiceContextHolder.getContext();
            return context.isAuthenticated();
        }

        public static boolean isSiteAdmin() {
            UserRole role = siteRole();
            return role == UserRole.ADMIN;
        }

        public static boolean canModifyResource(Long resourceOwnerId) {
            if (!isAuthenticated() || resourceOwnerId == null) {
                return false;
            }

            User currentUser = user();
            UserRole currentRole = siteRole();

            if (currentUser == null) {
                return false;
            }

            return currentRole == UserRole.ADMIN ||
                    currentUser.getId().equals(resourceOwnerId);
        }

        // Private constructor to prevent instantiation
        private Current() {}
    }
}