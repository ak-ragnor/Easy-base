package com.easyBase.domain.entity.user;

import com.easyBase.common.enums.UserRole;
import com.easyBase.common.enums.UserStatus;
import com.easyBase.domain.entity.base.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Enterprise User Entity
 *
 * Represents a user in the system with comprehensive user management features.
 *
 * Features:
 * - Extends AuditableEntity for automatic audit trail
 * - Uses database sequences for ID generation
 * - Includes comprehensive validation constraints
 * - Supports timezone-aware operations
 * - Optimized for performance with proper indexing
 * - Cacheable for improved performance
 *
 * Database Design:
 * - Table: users
 * - Primary Key: id (sequence-generated)
 * - Unique Constraints: email
 * - Indexes: email, role, status
 * - Audit Fields: created_at, last_modified, version
 *
 * @author Enterprise Team
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
        )
})
public class User extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    // ===== CORE USER FIELDS =====

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
     * User's role in the system
     * Determines access permissions and available features
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @NotNull(message = "Role is required")
    private UserRole role = UserRole.USER;

    /**
     * User's account status
     * Determines whether the user can access the system
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Status is required")
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * User's preferred timezone for date/time display
     * Used for timezone-aware operations and UI formatting
     */
    @Column(name = "user_timezone", length = 50)
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String userTimezone = "UTC";

    // ===== CONSTRUCTORS =====

    /**
     * Default constructor required by JPA
     */
    public User() {
        super();
    }

    /**
     * Constructor with required fields for business logic
     *
     * @param name  the user's full name
     * @param email the user's email address
     */
    public User(String name, String email) {
        super();
        this.name = name;
        this.email = email;
        this.role = UserRole.USER;
        this.status = UserStatus.ACTIVE;
        this.userTimezone = "UTC";
    }

    /**
     * Constructor with all fields except audit fields
     *
     * @param name         the user's full name
     * @param email        the user's email address
     * @param role         the user's role
     * @param status       the user's status
     * @param userTimezone the user's timezone
     */
    public User(String name, String email, UserRole role, UserStatus status, String userTimezone) {
        super();
        this.name = name;
        this.email = email;
        this.role = role != null ? role : UserRole.USER;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.userTimezone = userTimezone != null ? userTimezone : "UTC";
    }

    // ===== GETTERS AND SETTERS =====

    /**
     * Gets the user's full name
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's full name
     *
     * @param name the user's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's email address
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address
     *
     * @param email the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's role
     *
     * @return the user's role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Sets the user's role
     *
     * @param role the user's role
     */
    public void setRole(UserRole role) {
        this.role = role != null ? role : UserRole.USER;
    }

    /**
     * Gets the user's account status
     *
     * @return the user's status
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Sets the user's account status
     *
     * @param status the user's status
     */
    public void setStatus(UserStatus status) {
        this.status = status != null ? status : UserStatus.ACTIVE;
    }

    /**
     * Gets the user's timezone preference
     *
     * @return the user's timezone
     */
    public String getUserTimezone() {
        return userTimezone;
    }

    /**
     * Sets the user's timezone preference
     *
     * @param userTimezone the user's timezone
     */
    public void setUserTimezone(String userTimezone) {
        this.userTimezone = userTimezone != null ? userTimezone : "UTC";
    }

    // ===== BUSINESS METHODS =====

    /**
     * Checks if the user can log into the system
     *
     * @return true if the user can log in
     */
    public boolean canLogin() {
        return status != null && status.canLogin();
    }

    /**
     * Checks if the user account is active
     *
     * @return true if the user is active
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Checks if the user has administrative privileges
     *
     * @return true if the user is an admin or manager
     */
    public boolean isAdministrative() {
        return role != null && role.isAdministrative();
    }

    /**
     * Checks if the user is a system administrator
     *
     * @return true if the user is an admin
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    /**
     * Checks if the user can manage other users with the specified role
     *
     * @param targetRole the role to check management privileges for
     * @return true if the user can manage users with the target role
     */
    public boolean canManageRole(UserRole targetRole) {
        return role != null && role.canManage(targetRole);
    }

    /**
     * Activates the user account
     * Changes status to ACTIVE if reactivation is allowed
     */
    public void activate() {
        if (status != null && status.canReactivate()) {
            this.status = UserStatus.ACTIVE;
        }
    }

    /**
     * Deactivates the user account
     * Changes status to INACTIVE
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    /**
     * Locks the user account for security reasons
     * Changes status to LOCKED
     */
    public void lock() {
        this.status = UserStatus.LOCKED;
    }

    /**
     * Suspends the user account
     * Changes status to SUSPENDED
     */
    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }

    /**
     * Permanently disables the user account
     * Changes status to DISABLED
     */
    public void disable() {
        this.status = UserStatus.DISABLED;
    }

    /**
     * Gets the user's display name for UI purposes
     *
     * @return formatted display name
     */
    public String getDisplayName() {
        return String.format("%s (%s)", name, email);
    }

    /**
     * Gets a short identifier for the user
     *
     * @return short identifier
     */
    public String getShortIdentifier() {
        return String.format("User#%s", getId());
    }

    // ===== OBJECT METHODS =====

    /**
     * Enhanced string representation for debugging and logging
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("User{id=%s, name='%s', email='%s', role=%s, status=%s, timezone='%s', version=%s}",
                getId(), name, email, role, status, userTimezone, getVersion());
    }
}