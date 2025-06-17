package com.easyBase.service.security;

import com.easyBase.common.dto.auth.SiteLoginResponse;
import com.easyBase.service.exception.UnauthorizedException;

/**
 * Site Authentication Service Interface
 * Handles site-specific user authentication
 */
public interface SiteAuthenticationService {

    /**
     * Authenticate user for specific site by site ID
     * @param siteId Site identifier
     * @param email User email
     * @param password User password
     * @return Login response with JWT token
     * @throws UnauthorizedException if authentication fails
     */
    SiteLoginResponse authenticateUserForSite(Long siteId, String email, String password)
            throws UnauthorizedException;

    /**
     * Authenticate user for specific site by site code
     * @param siteCode Site code
     * @param email User email
     * @param password User password
     * @return Login response with JWT token
     * @throws UnauthorizedException if authentication fails
     */
    SiteLoginResponse authenticateUserForSite(String siteCode, String email, String password)
            throws UnauthorizedException;

    /**
     * Validate user has access to specific site
     * @param userId User identifier
     * @param siteId Site identifier
     * @throws UnauthorizedException if user not authorized
     */
    void validateUserSiteAccess(Long userId, Long siteId) throws UnauthorizedException;

    /**
     * Check if user is authorized for site
     * @param userId User identifier
     * @param siteId Site identifier
     * @return true if authorized
     */
    boolean isUserAuthorizedForSite(Long userId, Long siteId);

    /**
     * Refresh site session (optional for future implementation)
     * @param sessionId Session identifier
     * @throws UnauthorizedException if session invalid
     */
    default void refreshSiteSession(String sessionId) throws UnauthorizedException {
        throw new UnsupportedOperationException("Session refresh not implemented");
    }

    /**
     * Invalidate site session (optional for future implementation)
     * @param sessionId Session identifier
     */
    default void invalidateSiteSession(String sessionId) {
        // Default implementation - override if needed
    }
}