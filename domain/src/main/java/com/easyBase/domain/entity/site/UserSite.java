package com.easyBase.domain.entity.site;

import com.easyBase.common.enums.UserRole;
import com.easyBase.domain.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * UserSite Mapping Entity
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "user_sites",
        indexes = {
                @Index(name = "idx_user_sites_user", columnList = "user_id"),
                @Index(name = "idx_user_sites_site", columnList = "site_id"),
                @Index(name = "idx_user_sites_role", columnList = "site_role"),
                @Index(name = "idx_user_sites_active", columnList = "is_active"),
                @Index(name = "idx_user_sites_access_granted", columnList = "access_granted_at"),
                @Index(name = "idx_user_sites_user_active", columnList = "user_id, is_active"),
                @Index(name = "idx_user_sites_site_active", columnList = "site_id, is_active"),
                @Index(name = "idx_user_sites_site_role", columnList = "site_id, site_role")
        })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "userSiteCache")
@NamedQueries({
        @NamedQuery(
                name = "UserSite.findByUser",
                query = "SELECT us FROM UserSite us WHERE us.user = :user ORDER BY us.site.name"
        ),
        @NamedQuery(
                name = "UserSite.findBySite",
                query = "SELECT us FROM UserSite us WHERE us.site = :site ORDER BY us.user.name"
        ),
        @NamedQuery(
                name = "UserSite.findByUserAndSite",
                query = "SELECT us FROM UserSite us WHERE us.user = :user AND us.site = :site"
        ),
        @NamedQuery(
                name = "UserSite.findActiveByUser",
                query = "SELECT us FROM UserSite us WHERE us.user = :user AND us.isActive = true ORDER BY us.site.name"
        ),
        @NamedQuery(
                name = "UserSite.findActiveBySite",
                query = "SELECT us FROM UserSite us WHERE us.site = :site AND us.isActive = true ORDER BY us.user.name"
        ),
        @NamedQuery(
                name = "UserSite.findByUserAndRole",
                query = "SELECT us FROM UserSite us WHERE us.user = :user AND us.role = :role ORDER BY us.site.name"
        ),
        @NamedQuery(
                name = "UserSite.findBySiteAndRole",
                query = "SELECT us FROM UserSite us WHERE us.site = :site AND us.role = :role ORDER BY us.user.name"
        ),
        @NamedQuery(
                name = "UserSite.countByUser",
                query = "SELECT COUNT(us) FROM UserSite us WHERE us.user = :user"
        ),
        @NamedQuery(
                name = "UserSite.countBySite",
                query = "SELECT COUNT(us) FROM UserSite us WHERE us.site = :site"
        ),
        @NamedQuery(
                name = "UserSite.countActiveByUser",
                query = "SELECT COUNT(us) FROM UserSite us WHERE us.user = :user AND us.isActive = true"
        ),
        @NamedQuery(
                name = "UserSite.countActiveBySite",
                query = "SELECT COUNT(us) FROM UserSite us WHERE us.site = :site AND us.isActive = true"
        )
})
@IdClass(UserSiteId.class)
public class UserSite {

    /**
     * User in the relationship
     * Part of the composite primary key
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    /**
     * Site in the relationship
     * Part of the composite primary key
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    @NotNull(message = "Site is required")
    private Site site;

    /**
     * User's role within this specific site
     * Can be different from their global role
     * Allows site-specific permission management
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "site_role")
    private UserRole role;

    /**
     * Whether this user-site relationship is currently active
     * Allows for temporary suspension without deletion
     */
    @Column(name = "is_active", nullable = false)
    @NotNull(message = "Active status is required")
    private Boolean isActive = true;

    /**
     * When access to this site was granted to the user
     * Useful for audit trails and access tracking
     */
    @Column(name = "access_granted_at", nullable = false)
    @NotNull(message = "Access granted date is required")
    private ZonedDateTime accessGrantedAt;

    /**
     * When access to this site was revoked (if applicable)
     * Null if access is still active
     */
    @Column(name = "access_revoked_at")
    private ZonedDateTime accessRevokedAt;

    /**
     * User who granted access to this site
     * Used for audit trails and responsibility tracking
     */
    @Column(name = "granted_by_user_id")
    private Long grantedByUserId;

    /**
     * User who revoked access to this site (if applicable)
     * Used for audit trails and responsibility tracking
     */
    @Column(name = "revoked_by_user_id")
    private Long revokedByUserId;

    /**
     * Additional notes or comments about this user-site relationship
     * Can include reason for access, special conditions, etc.
     */
    @Column(name = "notes", length = 500)
    private String notes;

    // ===== AUDIT FIELDS (manually added since we can't extend AuditableEntity) =====

    /**
     * When this relationship was created
     */
    @Column(name = "created_at", nullable = false)
    @NotNull(message = "Created date is required")
    private ZonedDateTime createdAt;

    /**
     * When this relationship was last modified
     */
    @Column(name = "last_modified", nullable = false)
    @NotNull(message = "Last modified date is required")
    private ZonedDateTime lastModified;

    /**
     * Version field for optimistic locking
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    /**
     * Default constructor for JPA
     */
    public UserSite() {
        ZonedDateTime now = ZonedDateTime.now();
        this.createdAt = now;
        this.lastModified = now;
        this.accessGrantedAt = now;
        this.isActive = true;
        this.version = 0L;
    }

    /**
     * Constructor with required fields
     *
     * @param user The user
     * @param site The site
     */
    public UserSite(User user, Site site) {
        this();
        this.user = user;
        this.site = site;
    }

    /**
     * Constructor with role
     *
     * @param user     The user
     * @param site     The site
     * @param role The user's role within this site
     */
    public UserSite(User user, Site site, UserRole role) {
        this(user, site);
        this.role = role;
    }

    /**
     * Constructor with role and granted by user
     *
     * @param user           The user
     * @param site           The site
     * @param role       The user's role within this site
     * @param grantedByUserId ID of the user who granted access
     */
    public UserSite(User user, Site site, UserRole role, Long grantedByUserId) {
        this(user, site, role);
        this.grantedByUserId = grantedByUserId;
    }

    // ===== GETTERS AND SETTERS =====

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole siteRole) {
        this.role = siteRole;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ZonedDateTime getAccessGrantedAt() {
        return accessGrantedAt;
    }

    public void setAccessGrantedAt(ZonedDateTime accessGrantedAt) {
        this.accessGrantedAt = accessGrantedAt;
    }

    public ZonedDateTime getAccessRevokedAt() {
        return accessRevokedAt;
    }

    public void setAccessRevokedAt(ZonedDateTime accessRevokedAt) {
        this.accessRevokedAt = accessRevokedAt;
    }

    public Long getGrantedByUserId() {
        return grantedByUserId;
    }

    public void setGrantedByUserId(Long grantedByUserId) {
        this.grantedByUserId = grantedByUserId;
    }

    public Long getRevokedByUserId() {
        return revokedByUserId;
    }

    public void setRevokedByUserId(Long revokedByUserId) {
        this.revokedByUserId = revokedByUserId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * JPA callback to update lastModified timestamp before updates
     */
    @PreUpdate
    protected void onUpdate() {
        this.lastModified = ZonedDateTime.now();
    }

    /**
     * Activate this user-site relationship
     */
    public void activate() {
        this.isActive = true;
        this.accessRevokedAt = null;
        this.revokedByUserId = null;
    }

    /**
     * Deactivate this user-site relationship
     *
     * @param revokedByUserId ID of the user revoking access
     */
    public void deactivate(Long revokedByUserId) {
        this.isActive = false;
        this.accessRevokedAt = ZonedDateTime.now();
        this.revokedByUserId = revokedByUserId;
    }

    /**
     * Check if the user has an effective role within this site
     * Returns the site-specific role if set, otherwise the user's global role
     *
     * @return The effective role for this user-site relationship
     */
    public UserRole getEffectiveRole() {
        return role != null ? role : (user != null ? user.getRole() : null);
    }

    /**
     * Check if this relationship grants admin access to the site
     *
     * @return true if the effective role is ADMIN or SUPER_ADMIN
     */
    public boolean hasAdminAccess() {
        UserRole role = getEffectiveRole();
        return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN;
    }

    /**
     * Check if this relationship is currently valid
     * (active and within valid time range if applicable)
     *
     * @return true if the relationship is valid and active
     */
    public boolean isValidAccess() {
        return isActive != null && isActive &&
                accessGrantedAt != null &&
                accessGrantedAt.isBefore(ZonedDateTime.now()) &&
                (accessRevokedAt == null || accessRevokedAt.isAfter(ZonedDateTime.now()));
    }

    // ===== EQUALS, HASHCODE, AND TOSTRING =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSite)) return false;

        UserSite userSite = (UserSite) o;
        return user != null && user.equals(userSite.getUser()) &&
                site != null && site.equals(userSite.getSite());
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (site != null ? site.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserSite{" +
                "user=" + (user != null ? user.getEmail() : "null") +
                ", site=" + (site != null ? site.getCode() : "null") +
                ", siteRole=" + role +
                ", isActive=" + isActive +
                ", accessGrantedAt=" + accessGrantedAt +
                '}';
    }

    public void updateAssociation(UserRole role, String notes, Long updatedBy) {
        if (role != null) {
            this.role = role;
        }
        if (notes != null) {
            this.notes = notes;
        }
        if (updatedBy != null) {
            this.grantedByUserId = updatedBy;
        }
        this.lastModified = ZonedDateTime.now();
    }
}