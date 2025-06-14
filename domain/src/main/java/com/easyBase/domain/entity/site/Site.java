package com.easyBase.domain.entity.site;

import com.easyBase.common.enums.SiteStatus;
import com.easyBase.domain.entity.base.AuditableEntity;
import com.easyBase.domain.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashSet;
import java.util.Set;

/**
 * Enterprise Site Entity
 * Represents a site/tenant in the multi-tenant system with comprehensive site management features.
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "sites",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_sites_code", columnNames = "code")
        },
        indexes = {
                @Index(name = "idx_sites_code", columnList = "code"),
                @Index(name = "idx_sites_status", columnList = "status"),
                @Index(name = "idx_sites_timezone", columnList = "time_zone"),
                @Index(name = "idx_sites_language", columnList = "language_code"),
                @Index(name = "idx_sites_created_at", columnList = "created_at"),
                @Index(name = "idx_sites_status_code", columnList = "status, code")
        })
@SequenceGenerator(name = "entity_seq", sequenceName = "site_sequence", allocationSize = 1, initialValue = 2000)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "siteCache")
@NamedQueries({
        @NamedQuery(
                name = "Site.findByCode",
                query = "SELECT s FROM Site s WHERE s.code = :code"
        ),
        @NamedQuery(
                name = "Site.findByStatus",
                query = "SELECT s FROM Site s WHERE s.status = :status ORDER BY s.name"
        ),
        @NamedQuery(
                name = "Site.findActiveSites",
                query = "SELECT s FROM Site s WHERE s.status = com.easyBase.common.enums.SiteStatus.ACTIVE ORDER BY s.name"
        ),
        @NamedQuery(
                name = "Site.findAccessibleSites",
                query = "SELECT s FROM Site s WHERE s.status IN (com.easyBase.common.enums.SiteStatus.ACTIVE, com.easyBase.common.enums.SiteStatus.MAINTENANCE) ORDER BY s.name"
        ),
        @NamedQuery(
                name = "Site.countByStatus",
                query = "SELECT COUNT(s) FROM Site s WHERE s.status = :status"
        ),
        @NamedQuery(
                name = "Site.findByTimeZone",
                query = "SELECT s FROM Site s WHERE s.timeZone = :timeZone ORDER BY s.name"
        ),
        @NamedQuery(
                name = "Site.findByLanguageCode",
                query = "SELECT s FROM Site s WHERE s.languageCode = :languageCode ORDER BY s.name"
        )
})
public class Site extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Unique site code - used for identification and routing
     * Must be unique across the system and follow naming conventions
     * Used in URLs, configurations, and external integrations
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Site code is required and cannot be blank")
    @Size(min = 2, max = 50, message = "Site code must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Site code must contain only uppercase letters, numbers, and underscores")
    private String code;

    /**
     * Human-readable site name
     * Used in user interfaces and displays
     */
    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Site name is required and cannot be blank")
    @Size(min = 2, max = 100, message = "Site name must be between 2 and 100 characters")
    private String name;

    /**
     * Detailed description of the site
     * Provides context about the site's purpose and scope
     */
    @Column(name = "description", length = 500)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Current status of the site
     * Controls access and operational capabilities
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Site status is required")
    private SiteStatus status;

    /**
     * Site's timezone for date/time operations
     * Used for scheduling, timestamps, and user interface displays
     * Must be a valid timezone identifier (e.g., "America/New_York", "UTC")
     */
    @Column(name = "time_zone", nullable = false, length = 50)
    @NotBlank(message = "Timezone is required")
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timeZone;

    /**
     * Primary language code for the site
     * Used for localization and internationalization
     * Should follow ISO 639-1 format (e.g., "en", "es", "fr")
     */
    @Column(name = "language_code", nullable = false, length = 10)
    @NotBlank(message = "Language code is required")
    @Size(min = 2, max = 10, message = "Language code must be between 2 and 10 characters")
    @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$", message = "Language code must follow ISO format (e.g., 'en', 'en-US')")
    private String languageCode;

    /**
     * Users associated with this site
     * Many-to-many relationship managed through UserSite entity
     * Loaded lazily for performance optimization
     */
    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "siteUsersCache")
    private Set<UserSite> userSites = new HashSet<>();

    /**
     * Default constructor for JPA
     */
    public Site() {
        super();
    }

    /**
     * Constructor with required fields
     *
     * @param code         Unique site code
     * @param name         Site name
     * @param status       Site status
     * @param timeZone     Site timezone
     * @param languageCode Site language code
     */
    public Site(String code, String name, SiteStatus status, String timeZone, String languageCode) {
        this();
        this.code = code;
        this.name = name;
        this.status = status;
        this.timeZone = timeZone;
        this.languageCode = languageCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SiteStatus getStatus() {
        return status;
    }

    public void setStatus(SiteStatus status) {
        this.status = status;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Set<UserSite> getUserSites() {
        return userSites;
    }

    public void setUserSites(Set<UserSite> userSites) {
        this.userSites = userSites;
    }

    /**
     * Check if the site is accessible to users
     *
     * @return true if site allows user access
     */
    public boolean isAccessible() {
        return status != null && status.isAccessible();
    }

    /**
     * Check if the site allows data modifications
     *
     * @return true if site allows data modifications
     */
    public boolean isModifiable() {
        return status != null && status.isModifiable();
    }

    /**
     * Check if the site is operational
     *
     * @return true if site is in operational state
     */
    public boolean isOperational() {
        return status != null && status.isOperational();
    }

    /**
     * Add a user to this site
     *
     * @param user The user to add
     * @return The created UserSite relationship
     */
    public UserSite addUser(User user) {
        UserSite userSite = new UserSite(user, this);
        this.userSites.add(userSite);
        if (user.getUserSites() != null) {
            user.getUserSites().add(userSite);
        }
        return userSite;
    }

    /**
     * Remove a user from this site
     *
     * @param user The user to remove
     * @return true if the user was removed
     */
    public boolean removeUser(User user) {
        UserSite userSiteToRemove = null;
        for (UserSite userSite : this.userSites) {
            if (userSite.getUser().equals(user)) {
                userSiteToRemove = userSite;
                break;
            }
        }

        if (userSiteToRemove != null) {
            this.userSites.remove(userSiteToRemove);
            if (user.getUserSites() != null) {
                user.getUserSites().remove(userSiteToRemove);
            }
            return true;
        }
        return false;
    }

    /**
     * Check if a user is associated with this site
     *
     * @param user The user to check
     * @return true if the user is associated with this site
     */
    public boolean hasUser(User user) {
        return userSites.stream()
                .anyMatch(userSite -> userSite.getUser().equals(user));
    }

    /**
     * Get all users associated with this site
     *
     * @return Set of users
     */
    public Set<User> getUsers() {
        return userSites.stream()
                .map(UserSite::getUser)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Site)) return false;

        Site site = (Site) o;
        return code != null && code.equals(site.getCode());
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Site{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", timeZone='" + timeZone + '\'' +
                ", languageCode='" + languageCode + '\'' +
                '}';
    }
}