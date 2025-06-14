package com.easyBase.domain.entity.base;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Base Entity providing common functionality for all entities
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key using database sequences for enterprise scalability
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * Version field for optimistic locking to prevent concurrent modification issues
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    // ===== CONSTRUCTORS =====

    /**
     * Default constructor required by JPA
     */
    protected BaseEntity() {
        // JPA requirement
    }

    /**
     * Constructor with ID for testing purposes
     *
     * @param id the entity ID
     */
    protected BaseEntity(Long id) {
        this.id = id;
    }

    // ===== GETTERS AND SETTERS =====

    /**
     * Gets the entity ID
     *
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the entity ID (should only be used by JPA or tests)
     *
     * @param id the unique identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the optimistic locking version
     *
     * @return the version number
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the optimistic locking version (managed by JPA)
     *
     * @param version the version number
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    // ===== BUSINESS METHODS =====

    /**
     * Checks if this entity is new (not yet persisted)
     *
     * @return true if the entity is new (ID is null)
     */
    public boolean isNew() {
        return getId() == null;
    }

    /**
     * Checks if this entity has been persisted
     *
     * @return true if the entity has been persisted (ID is not null)
     */
    public boolean isPersisted() {
        return getId() != null;
    }

    // ===== OBJECT METHODS =====

    /**
     * Enterprise-standard equals method based on ID
     *
     * Two entities are equal if:
     * 1. They are the same instance
     * 2. They are of the same class and have the same non-null ID
     *
     * @param obj the object to compare
     * @return true if entities are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        BaseEntity that = (BaseEntity) obj;

        // For new entities, use identity equality
        if (this.getId() == null || that.getId() == null) {
            return false;
        }

        // For persisted entities, compare IDs
        return Objects.equals(this.getId(), that.getId());
    }

    /**
     * Enterprise-standard hashCode method
     *
     * Uses a constant value to ensure hash code doesn't change
     * when entity is persisted (ID changes from null to actual value)
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return getClass().hashCode() * 31 + (getId() != null ? getId().hashCode() : 0);
    }

    /**
     * String representation for debugging and logging
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("%s{id=%s, version=%s}",
                getClass().getSimpleName(), getId(), getVersion());
    }
}