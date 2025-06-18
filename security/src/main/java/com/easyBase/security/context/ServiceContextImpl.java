package com.easyBase.security.context;

import com.easyBase.common.enums.UserRole;
import com.easyBase.common.security.ServiceContext;
import com.easyBase.security.jwt.JwtTokenClaims;

import java.time.ZonedDateTime;

/**
 * ServiceContext Implementation
 * Provides access to current user and site information from JWT claims
 */
public class ServiceContextImpl implements ServiceContext {

    private final JwtTokenClaims tokenClaims;

    public ServiceContextImpl(JwtTokenClaims tokenClaims) {
        this.tokenClaims = tokenClaims;
    }

    @Override
    public Long getCurrentUserId() {
        return tokenClaims != null ? tokenClaims.getUserId() : null;
    }

    @Override
    public String getCurrentUserEmail() {
        return tokenClaims != null ? tokenClaims.getUserEmail() : null;
    }

    @Override
    public Long getCurrentSiteId() {
        return tokenClaims != null ? tokenClaims.getSiteId() : null;
    }

    @Override
    public String getCurrentSiteCode() {
        return tokenClaims != null ? tokenClaims.getSiteCode() : null;
    }

    @Override
    public String getSessionId() {
        return tokenClaims != null ? tokenClaims.getSessionId() : null;
    }

    @Override
    public boolean isAuthenticated() {
        return tokenClaims != null &&
                tokenClaims.getUserId() != null &&
                tokenClaims.getSiteId() != null;
    }

    @Override
    public UserRole getCurrentUserRole() {
        return tokenClaims != null ? tokenClaims.getUserRole() : null;
    }

    @Override
    public UserRole getCurrentSiteRole() {
        return tokenClaims != null ? tokenClaims.getSiteRole() : null;
    }

    @Override
    public ZonedDateTime getSessionExpiry() {
        return tokenClaims != null ? tokenClaims.getExpiresAt() : null;
    }

    @Override
    public String toString() {
        return "ServiceContextImpl{" +
                "userId=" + getCurrentUserId() +
                ", userEmail=" + getCurrentUserEmail() +
                ", siteId=" + getCurrentSiteId() +
                ", siteCode=" + getCurrentSiteCode() +
                ", authenticated=" + isAuthenticated() +
                '}';
    }
}