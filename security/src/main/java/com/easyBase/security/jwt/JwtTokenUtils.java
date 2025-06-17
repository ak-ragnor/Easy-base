package com.easyBase.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utility class for JWT token operations
 * Provides convenience methods for token management
 */
@Component
public class JwtTokenUtils {

    @Autowired
    private SiteJwtTokenProvider tokenProvider;

    /**
     * Extract user ID from JWT token
     */
    public Long extractUserId(String token) {
        try {
            JwtTokenClaims claims = tokenProvider.validateTokenAndGetClaims(token);
            return claims.getUserId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract site ID from JWT token
     */
    public Long extractSiteId(String token) {
        try {
            JwtTokenClaims claims = tokenProvider.validateTokenAndGetClaims(token);
            return claims.getSiteId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract site code from JWT token
     */
    public String extractSiteCode(String token) {
        try {
            JwtTokenClaims claims = tokenProvider.validateTokenAndGetClaims(token);
            return claims.getSiteCode();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if token is valid (not expired and properly signed)
     */
    public boolean isTokenValid(String token) {
        try {
            tokenProvider.validateTokenAndGetClaims(token);
            return !tokenProvider.isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if token belongs to specific site
     */
    public boolean isTokenForSite(String token, Long siteId) {
        Long tokenSiteId = extractSiteId(token);
        return tokenSiteId != null && tokenSiteId.equals(siteId);
    }

    /**
     * Check if token belongs to specific user
     */
    public boolean isTokenForUser(String token, Long userId) {
        Long tokenUserId = extractUserId(token);
        return tokenUserId != null && tokenUserId.equals(userId);
    }
}