package com.easyBase.security.context;

import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
import com.easyBase.domain.entity.user.User;
import com.easyBase.common.enums.UserRole;
import com.easyBase.security.jwt.JwtTokenClaims;

import java.time.ZonedDateTime;

/**
 * ServiceContext Implementation
 * Provides access to current user and site information
 */
public class ServiceContextImpl implements ServiceContext {

    private final User currentUser;
    private final Site currentSite;
    private final UserSite userSiteAssociation;
    private final JwtTokenClaims tokenClaims;

    public ServiceContextImpl() {
        this.currentUser = null;
        this.currentSite = null;
        this.userSiteAssociation = null;
        this.tokenClaims = null;
    }

    public ServiceContextImpl(User user, Site site, UserSite userSite, JwtTokenClaims claims) {
        this.currentUser = user;
        this.currentSite = site;
        this.userSiteAssociation = userSite;
        this.tokenClaims = claims;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public Site getCurrentSite() {
        return currentSite;
    }

    @Override
    public UserSite getUserSiteAssociation() {
        return userSiteAssociation;
    }

    @Override
    public String getSessionId() {
        return tokenClaims != null ? tokenClaims.getSessionId() : null;
    }

    @Override
    public boolean hasCurrentUser() {
        return currentUser != null;
    }

    @Override
    public boolean hasCurrentSite() {
        return currentSite != null;
    }

    @Override
    public boolean isAuthenticated() {
        return hasCurrentUser() && hasCurrentSite() && userSiteAssociation != null;
    }

    @Override
    public UserRole getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    @Override
    public UserRole getCurrentSiteRole() {
        return userSiteAssociation != null ? userSiteAssociation.getRole() : null;
    }

    @Override
    public ZonedDateTime getSessionExpiry() {
        return tokenClaims != null ? tokenClaims.getExpiresAt() : null;
    }

    @Override
    public String toString() {
        return "ServiceContextImpl{" +
                "user=" + (currentUser != null ? currentUser.getEmail() : "null") +
                ", site=" + (currentSite != null ? currentSite.getCode() : "null") +
                ", authenticated=" + isAuthenticated() +
                '}';
    }
}