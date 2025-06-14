package com.easyBase.domain.entity.user;

import com.easyBase.common.enums.UserRole;
import com.easyBase.common.enums.UserStatus;
import com.easyBase.domain.entity.base.AuditableEntity;
import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enterprise User Entity
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_role", columnList = "role"),
                @Index(name = "idx_users_status", columnList = "status"),
                @Index(name = "idx_users_timezone", columnList = "user_timezone"),
                @Index(name = "idx_users_created_at", columnList = "created_at"),
                @Index(name = "idx_users_role_status", columnList = "role, status")
        })
@SequenceGenerator(name = "entity_seq", sequenceName = "user_sequence", allocationSize = 1, initialValue = 1000)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "userCache")
@NamedQueries({
        @NamedQuery(
                name = "User.findByEmail",
                query = "SELECT u FROM User u WHERE u.email = :email"
        ),
        @NamedQuery(
                name = "User.findByRole",
                query = "SELECT u FROM User u WHERE u.role = :role ORDER BY u.name"
        ),
        @NamedQuery(
                name = "User.findByStatus",
                query = "SELECT u FROM User u WHERE u.status = :status ORDER BY u.lastModified DESC"
        ),
        @NamedQuery(
                name = "User.findActiveUsers",
                query = "SELECT u FROM User u WHERE u.status = com.easyBase.common.enums.UserStatus.ACTIVE ORDER BY u.name"
        ),
        @NamedQuery(
                name = "User.countByRole",
                query = "SELECT COUNT(u) FROM User u WHERE u.role = :role"
        ),
        @NamedQuery(
                name = "User.countByStatus",
                query = "SELECT COUNT(u) FROM User u WHERE u.status = :status"
        ),
        @NamedQuery(
                name = "User.findBySite",
                query = "SELECT DISTINCT us.user FROM UserSite us WHERE us.site = :site AND us.isActive = true ORDER BY us.user.name"
        ),
        @NamedQuery(
                name = "User.findBySiteCode",
                query = "SELECT DISTINCT us.user FROM UserSite us WHERE us.site.code = :siteCode AND us.isActive = true ORDER BY us.user.name"
        ),
        @NamedQuery(
                name = "User.countBySite",
                query = "SELECT COUNT(DISTINCT us.user) FROM UserSite us WHERE us.site = :site AND us.isActive = true"
        )
})
public class User extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    /**
     * User's full name
     * Required field with length constraints for database optimization
     */
    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Name is required and cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * User's email address - used as unique identifier for login
     * Must be unique across the system and properly formatted
     */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    @NotBlank(message = "Email is required and cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    /**
     * User's global role in the system
     * Determines base access permissions and available features
     * Can be overridden by site-specific roles in UserSite
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @NotNull(message = "User role is required")
    private UserRole role;

    /**
     * Current status of the user account
     * Controls whether the user can access the system
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "User status is required")
    private UserStatus status;

    /**
     * User's preferred timezone for date/time displays
     * Used for scheduling, timestamps, and user interface displays
     * Must be a valid timezone identifier (e.g., "America/New_York", "UTC")
     */
    @Column(name = "user_timezone", length = 50)
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String userTimezone;

    /**
     * User's preferred language code for localization
     * Should follow ISO 639-1 format (e.g., "en", "es", "fr")
     */
    @Column(name = "language_preference", length = 10)
    @Size(max = 10, message = "Language preference must not exceed 10 characters")
    private String languagePreference;

    /**
     * User's phone number for contact and security purposes
     */
    @Column(name = "phone_number", length = 20)
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    /**
     * Whether the user's email address has been verified
     */
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    /**
     * Whether the user's phone number has been verified
     */
    @Column(name = "phone_verified", nullable = false)
    private Boolean phoneVerified = false;

    /**
     * Whether two-factor authentication is enabled for this user
     */
    @Column(name = "two_factor_enabled", nullable = false)
    private Boolean twoFactorEnabled = false;

    // ===== RELATIONSHIPS =====

    /**
     * Sites associated with this user
     * Many-to-many relationship managed through UserSite entity
     * Loaded lazily for performance optimization
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "userSitesCache")
    private Set<UserSite> userSites = new HashSet<>();

    // ===== CONSTRUCTORS =====

    /**
     * Default constructor for JPA
     */
    public User() {
        super();
        this.emailVerified = false;
        this.phoneVerified = false;
        this.twoFactorEnabled = false;
    }

    /**
     * Constructor with required fields
     *
     * @param name   User's full name
     * @param email  User's email address
     * @param role   User's role
     * @param status User's status
     */
    public User(String name, String email, UserRole role, UserStatus status) {
        this();
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getUserTimezone() {
        return userTimezone;
    }

    public void setUserTimezone(String userTimezone) {
        this.userTimezone = userTimezone;
    }

    public String getLanguagePreference() {
        return languagePreference;
    }

    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public Set<UserSite> getUserSites() {
        return userSites;
    }

    public void setUserSites(Set<UserSite> userSites) {
        this.userSites = userSites;
    }

    // ===== SITE-RELATED BUSINESS METHODS =====

    /**
     * Add this user to a site
     *
     * @param site The site to add the user to
     * @return The created UserSite relationship
     */
    public UserSite addToSite(Site site) {
        UserSite userSite = new UserSite(this, site);
        this.userSites.add(userSite);
        if (site.getUserSites() != null) {
            site.getUserSites().add(userSite);
        }
        return userSite;
    }

    /**
     * Add this user to a site with a specific role
     *
     * @param site     The site to add the user to
     * @param siteRole The role for this user within the site
     * @return The created UserSite relationship
     */
    public UserSite addToSite(Site site, UserRole siteRole) {
        UserSite userSite = new UserSite(this, site, siteRole);
        this.userSites.add(userSite);
        if (site.getUserSites() != null) {
            site.getUserSites().add(userSite);
        }
        return userSite;
    }

    /**
     * Remove this user from a site
     *
     * @param site The site to remove the user from
     * @return true if the user was removed
     */
    public boolean removeFromSite(Site site) {
        UserSite userSiteToRemove = null;
        for (UserSite userSite : this.userSites) {
            if (userSite.getSite().equals(site)) {
                userSiteToRemove = userSite;
                break;
            }
        }

        if (userSiteToRemove != null) {
            this.userSites.remove(userSiteToRemove);
            if (site.getUserSites() != null) {
                site.getUserSites().remove(userSiteToRemove);
            }
            return true;
        }
        return false;
    }

    /**
     * Check if this user has access to a specific site
     *
     * @param site The site to check
     * @return true if the user has active access to the site
     */
    public boolean hasAccessToSite(Site site) {
        return userSites.stream()
                .anyMatch(userSite -> userSite.getSite().equals(site) &&
                        userSite.getIsActive() &&
                        userSite.isValidAccess());
    }

    /**
     * Check if this user has access to a site by site code
     *
     * @param siteCode The site code to check
     * @return true if the user has active access to the site
     */
    public boolean hasAccessToSite(String siteCode) {
        return userSites.stream()
                .anyMatch(userSite -> userSite.getSite().getCode().equals(siteCode) &&
                        userSite.getIsActive() &&
                        userSite.isValidAccess());
    }

    /**
     * Get the user's role within a specific site
     * Returns the site-specific role if set, otherwise the user's global role
     *
     * @param site The site to check
     * @return The effective role for this user within the site
     */
    public UserRole getRoleInSite(Site site) {
        return userSites.stream()
                .filter(userSite -> userSite.getSite().equals(site) &&
                        userSite.getIsActive() &&
                        userSite.isValidAccess())
                .map(UserSite::getEffectiveRole)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get all sites this user has access to
     *
     * @return Set of sites the user can access
     */
    public Set<Site> getAccessibleSites() {
        return userSites.stream()
                .filter(userSite -> userSite.getIsActive() && userSite.isValidAccess())
                .map(UserSite::getSite)
                .collect(Collectors.toSet());
    }

    /**
     * Get all active UserSite relationships
     *
     * @return Set of active UserSite relationships
     */
    public Set<UserSite> getActiveUserSites() {
        return userSites.stream()
                .filter(userSite -> userSite.getIsActive() && userSite.isValidAccess())
                .collect(Collectors.toSet());
    }

    /**
     * Check if this user has admin access to any site
     *
     * @return true if the user has admin access to at least one site
     */
    public boolean hasAdminAccessToAnySite() {
        return userSites.stream()
                .anyMatch(userSite -> userSite.getIsActive() &&
                        userSite.isValidAccess() &&
                        userSite.hasAdminAccess());
    }

    /**
     * Check if this user has admin access to a specific site
     *
     * @param site The site to check
     * @return true if the user has admin access to the site
     */
    public boolean hasAdminAccessToSite(Site site) {
        return userSites.stream()
                .anyMatch(userSite -> userSite.getSite().equals(site) &&
                        userSite.getIsActive() &&
                        userSite.isValidAccess() &&
                        userSite.hasAdminAccess());
    }

    // ===== USER STATUS BUSINESS METHODS =====

    /**
     * Check if the user is active and can access the system
     *
     * @return true if the user status allows system access
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Check if the user account is locked
     *
     * @return true if the user account is locked
     */
    public boolean isLocked() {
        return status == UserStatus.LOCKED;
    }

    /**
     * Check if the user account is suspended
     *
     * @return true if the user account is suspended
     */
    public boolean isSuspended() {
        return status == UserStatus.SUSPENDED;
    }

    /**
     * Check if the user has completed email verification
     *
     * @return true if email is verified
     */
    public boolean isEmailVerified() {
        return emailVerified != null && emailVerified;
    }

    /**
     * Check if the user has completed phone verification
     *
     * @return true if phone is verified
     */
    public boolean isPhoneVerified() {
        return phoneVerified != null && phoneVerified;
    }

    /**
     * Check if two-factor authentication is enabled
     *
     * @return true if 2FA is enabled
     */
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled != null && twoFactorEnabled;
    }

    // ===== EQUALS, HASHCODE, AND TOSTRING =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;
        return email != null && email.equals(user.getEmail());
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", sitesCount=" + (userSites != null ? userSites.size() : 0) +
                '}';
    }
}