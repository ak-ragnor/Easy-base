package com.easyBase.security.jwt;

import com.easyBase.common.enums.UserRole;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * JWT Token Claims structure for site authentication
 */
public class JwtTokenClaims {

    private Long userId;
    private String userEmail;
    private String userName;
    private UserRole userRole;
    private Long siteId;
    private String siteCode;
    private String siteName;
    private UserRole siteRole;
    private ZonedDateTime issuedAt;
    private ZonedDateTime expiresAt;
    private String sessionId;
    private List<String> authorities;

    // Constructors
    public JwtTokenClaims() {}

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public UserRole getSiteRole() {
        return siteRole;
    }

    public void setSiteRole(UserRole siteRole) {
        this.siteRole = siteRole;
    }

    public ZonedDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(ZonedDateTime issuedAt) {
        this.issuedAt = issuedAt;
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

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "JwtTokenClaims{" +
                "userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", userRole=" + userRole +
                ", siteId=" + siteId +
                ", siteCode='" + siteCode + '\'' +
                ", siteName='" + siteName + '\'' +
                ", siteRole=" + siteRole +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
