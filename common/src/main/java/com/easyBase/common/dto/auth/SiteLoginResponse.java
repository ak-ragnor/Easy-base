package com.easyBase.common.dto.auth;

import com.easyBase.common.dto.site.SiteDTO;
import com.easyBase.common.dto.user.UserDTO;
import com.easyBase.common.enums.UserRole;

import java.time.ZonedDateTime;

/**
 * Site Login Response DTO
 */
public class SiteLoginResponse {

    private boolean success;
    private String message;
    private String token;
    private UserDTO user;
    private SiteDTO site;
    private UserRole siteRole;
    private ZonedDateTime expiresAt;
    private String sessionId;

    public SiteLoginResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public SiteDTO getSite() {
        return site;
    }

    public void setSite(SiteDTO site) {
        this.site = site;
    }

    public UserRole getSiteRole() {
        return siteRole;
    }

    public void setSiteRole(UserRole siteRole) {
        this.siteRole = siteRole;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "SiteLoginResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", user=" + user +
                ", site=" + site +
                ", siteRole=" + siteRole +
                ", expiresAt=" + expiresAt +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}