//package com.easyBase.security.authorization;
//
//import com.easyBase.security.context.ServiceContext;
//import com.easyBase.security.context.ServiceContextHolder;
//import com.easyBase.domain.entity.user.User;
//import com.easyBase.domain.entity.site.Site;
//import com.easyBase.common.enums.UserRole;
//import com.easyBase.service.exception.UnauthorizedException;
//import org.springframework.stereotype.Component;
//
///**
// * Site-specific authorization validator
// * Provides reusable validation methods for business services
// */
//@Component
//public class SiteAuthorizationValidator {
//
//    /**
//     * Validate user has access to current site
//     */
//    public void validateSiteAccess() throws UnauthorizedException {
//        ServiceContext context = ServiceContextHolder.getContext();
//
//        if (!context.isAuthenticated()) {
//            throw new UnauthorizedException("Authentication required");
//        }
//
//        User currentUser = context.getCurrentUser();
//        Site currentSite = context.getCurrentSite();
//
//        if (currentUser == null) {
//            throw new UnauthorizedException("User context not available");
//        }
//
//        if (currentSite == null) {
//            throw new UnauthorizedException("Site context not available");
//        }
//    }
//
//    /**
//     * Validate user has specific role in current site
//     */
//    public void validateSiteRole(UserRole requiredRole) throws UnauthorizedException {
//        validateSiteAccess();
//
//        UserRole currentRole = ServiceContext.Current.siteRole();
//
//        if (currentRole == null) {
//            throw new UnauthorizedException("Site role not available");
//        }
//
//        if (!hasRequiredRole(currentRole, requiredRole)) {
//            throw new UnauthorizedException("Insufficient role for this operation. Required: " +
//                    requiredRole + ", Current: " + currentRole);
//        }
//    }
//
//
//    /**
//     * Validate user can access resource in current site
//     */
//    public void validateSiteResourceAccess(Long resourceSiteId) throws UnauthorizedException {
//        if (!ServiceContext.Current.isAuthenticated()) {
//            throw new UnauthorizedException("Authentication required");
//        }
//
//        Site currentSite = ServiceContext.Current.site();
//
//        if (currentSite == null) {
//            throw new UnauthorizedException("Site context not available");
//        }
//
//        if (!currentSite.getId().equals(resourceSiteId)) {
//            throw new UnauthorizedException("Resource does not belong to current site");
//        }
//    }
//
//    /**
//     * Check if current user is admin in current site
//     */
//    public boolean isCurrentUserSiteAdmin() {
//        try {
//            return ServiceContext.Current.isSiteAdmin();
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * Check if current user can perform admin operations
//     */
//    public boolean canPerformAdminOperations() {
//        return isCurrentUserSiteAdmin();
//    }
//
//    /**
//     * Check if current user can modify specific resource
//     */
//    public boolean canModifyResource(Long resourceOwnerId) {
//        try {
//            // FIXED: Use static method directly
//            return ServiceContext.Current.canModifyResource(resourceOwnerId);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * Check if current user has specific role
//     */
//    public boolean hasRole(UserRole role) {
//        try {
//            UserRole currentRole = ServiceContext.Current.siteRole();
//            return currentRole == role ||
//                    (currentRole == UserRole.ADMIN && role == UserRole.USER);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * Check if current user has at least the specified role level
//     */
//    public boolean hasAtLeastRole(UserRole minimumRole) {
//        try {
//            UserRole currentRole = ServiceContext.Current.siteRole();
//            return hasRequiredRole(currentRole, minimumRole);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * Validate user has admin role
//     */
//    public void requireAdminRole() throws UnauthorizedException {
//        validateSiteRole(UserRole.ADMIN);
//    }
//
//    /**
//     * Validate user has at least user role (basically any authenticated user)
//     */
//    public void requireUserRole() throws UnauthorizedException {
//        validateSiteRole(UserRole.USER);
//    }
//
//    /**
//     * Check if current role satisfies required role
//     * Admin can do everything User can do
//     */
//    private boolean hasRequiredRole(UserRole currentRole, UserRole requiredRole) {
//        if (currentRole == null || requiredRole == null) {
//            return false;
//        }
//
//        if (currentRole == requiredRole) {
//            return true;
//        }
//
//        return currentRole == UserRole.ADMIN && requiredRole == UserRole.USER;
//    }
//}