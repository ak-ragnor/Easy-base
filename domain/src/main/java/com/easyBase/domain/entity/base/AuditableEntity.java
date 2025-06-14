package com.easyBase.domain.entity.base;

import jakarta.persistence.*;

import java.io.Serial;
import java.time.ZonedDateTime;

/**
 * Auditable Entity providing audit trail functionality
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
@MappedSuperclass
@EntityListeners(AuditableEntity.AuditListener.class)
public abstract class AuditableEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Timestamp when the entity was first created
     * Immutable after initial persistence
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Timestamp when the entity was last modified
     * Automatically updated on each save operation
     */
    @Column(name = "last_modified", nullable = false)
    private ZonedDateTime lastModified;

    // ===== CONSTRUCTORS =====

    /**
     * Default constructor required by JPA
     */
    protected AuditableEntity() {
        super();
    }

    /**
     * Constructor with ID for testing purposes
     *
     * @param id the entity ID
     */
    protected AuditableEntity(Long id) {
        super(id);
    }

    // ===== GETTERS AND SETTERS =====

    /**
     * Gets the creation timestamp
     *
     * @return when the entity was created
     */
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp (should only be used by JPA or tests)
     *
     * @param createdAt when the entity was created
     */
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last modification timestamp
     *
     * @return when the entity was last modified
     */
    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    /**
     * Sets the last modification timestamp (should only be used by JPA or tests)
     *
     * @param lastModified when the entity was last modified
     */
    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    // ===== BUSINESS METHODS =====

    /**
     * Manually touch the entity to update the last modified timestamp
     * Useful for cascade updates or business logic that doesn't change fields
     */
    public void touch() {
        this.lastModified = ZonedDateTime.now();
    }

    /**
     * Check if the entity was recently created (within the last hour)
     *
     * @return true if created within the last hour
     */
    public boolean isRecentlyCreated() {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(ZonedDateTime.now().minusHours(1));
    }

    /**
     * Check if the entity was recently modified (within the last hour)
     *
     * @return true if modified within the last hour
     */
    public boolean isRecentlyModified() {
        if (lastModified == null) {
            return false;
        }
        return lastModified.isAfter(ZonedDateTime.now().minusHours(1));
    }

    // ===== OBJECT METHODS =====

    /**
     * Enhanced string representation including audit information
     *
     * @return string representation with audit info
     */
    @Override
    public String toString() {
        return String.format("%s{id=%s, version=%s, createdAt=%s, lastModified=%s}",
                getClass().getSimpleName(), getId(), getVersion(), createdAt, lastModified);
    }

    // ===== JPA LIFECYCLE CALLBACKS =====

    /**
     * Enterprise Audit Listener for automatic timestamp management
     *
     * This listener ensures that audit timestamps are properly managed
     * without requiring manual intervention in service classes.
     */
    public static class AuditListener {

        /**
         * Called before entity is persisted for the first time
         * Sets both created and modified timestamps to current time
         *
         * @param entity the entity being persisted
         */
        @PrePersist
        public void prePersist(AuditableEntity entity) {
            ZonedDateTime now = ZonedDateTime.now();
            entity.setCreatedAt(now);
            entity.setLastModified(now);
        }

        /**
         * Called before entity is updated
         * Updates only the last modified timestamp
         *
         * @param entity the entity being updated
         */
        @PreUpdate
        public void preUpdate(AuditableEntity entity) {
            entity.setLastModified(ZonedDateTime.now());
        }
    }
}