package com.easybase.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested entity is not found.
 * This exception is mapped to HTTP 404 (Not Found) in REST responses.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String entityType;
    private final String identifier;

    /**
     * Constructs a new entity not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public EntityNotFoundException(String message) {
        super(message);
        this.entityType = null;
        this.identifier = null;
    }

    /**
     * Constructs a new entity not found exception with entity type and identifier.
     *
     * @param entityType the type of entity that wasn't found
     * @param identifier the identifier that was used to look up the entity
     */
    public EntityNotFoundException(String entityType, String identifier) {
        super(String.format("%s not found with identifier: %s", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }

    /**
     * Constructs a new entity not found exception with a cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.entityType = null;
        this.identifier = null;
    }

    /**
     * Gets the entity type that wasn't found.
     *
     * @return the entity type
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Gets the identifier that was used to look up the entity.
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }
}