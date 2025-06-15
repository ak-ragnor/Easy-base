package com.easyBase.common.dto.site;

import com.easyBase.common.dto.user.UserDTO;
import com.easyBase.common.enums.UserRole;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * UserSite DTO
 *
 * Data Transfer Object representing a user-site relationship.
 * Used for managing user access to sites.
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
public class UserSiteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ZonedDateTime createdAt;
    private ZonedDateTime lastModified;
    private Long version;

    private UserDTO user;
    private SiteDTO site;
    private UserRole siteRole;
    private UserRole effectiveRole;
    private Boolean isActive;
    private ZonedDateTime accessGrantedAt;
    private ZonedDateTime accessRevokedAt;
    private Long grantedByUserId;
    private String grantedByUserName;
    private Long revokedByUserId;
    private String revokedByUserName;
    private String notes;
    private boolean validAccess;
    private boolean adminAccess;

    // Constructors
    public UserSiteDTO() {
    }

    public UserSiteDTO(UserDTO user, SiteDTO site, Boolean isActive) {
        this.user = user;
        this.site = site;
        this.isActive = isActive;
    }

    // Getters and Setters for audit fields
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getLastModified() { return lastModified; }
    public void setLastModified(ZonedDateTime lastModified) { this.lastModified = lastModified; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    // Getters and Setters for relationship fields
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public SiteDTO getSite() { return site; }
    public void setSite(SiteDTO site) { this.site = site; }

    public UserRole getSiteRole() { return siteRole; }
    public void setSiteRole(UserRole siteRole) { this.siteRole = siteRole; }

    public UserRole getEffectiveRole() { return effectiveRole; }
    public void setEffectiveRole(UserRole effectiveRole) { this.effectiveRole = effectiveRole; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public ZonedDateTime getAccessGrantedAt() { return accessGrantedAt; }
    public void setAccessGrantedAt(ZonedDateTime accessGrantedAt) { this.accessGrantedAt = accessGrantedAt; }

    public ZonedDateTime getAccessRevokedAt() { return accessRevokedAt; }
    public void setAccessRevokedAt(ZonedDateTime accessRevokedAt) { this.accessRevokedAt = accessRevokedAt; }

    public Long getGrantedByUserId() { return grantedByUserId; }
    public void setGrantedByUserId(Long grantedByUserId) { this.grantedByUserId = grantedByUserId; }

    public String getGrantedByUserName() { return grantedByUserName; }
    public void setGrantedByUserName(String grantedByUserName) { this.grantedByUserName = grantedByUserName; }

    public Long getRevokedByUserId() { return revokedByUserId; }
    public void setRevokedByUserId(Long revokedByUserId) { this.revokedByUserId = revokedByUserId; }

    public String getRevokedByUserName() { return revokedByUserName; }
    public void setRevokedByUserName(String revokedByUserName) { this.revokedByUserName = revokedByUserName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isValidAccess() { return validAccess; }
    public void setValidAccess(boolean validAccess) { this.validAccess = validAccess; }

    public boolean isAdminAccess() { return adminAccess; }
    public void setAdminAccess(boolean adminAccess) { this.adminAccess = adminAccess; }

    @Override
    public String toString() {
        return "UserSiteDTO{" +
                "user=" + (user != null ? user.getEmail() : "null") +
                ", site=" + (site != null ? site.getCode() : "null") +
                ", effectiveRole=" + effectiveRole +
                ", isActive=" + isActive +
                ", validAccess=" + validAccess +
                '}';
    }
}
