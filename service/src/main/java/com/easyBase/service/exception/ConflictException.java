package com.easyBase.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Conflict exception
 *
 * Thrown when the request conflicts with the current state of the resource.
 * Results in HTTP 409 Conflict responses.
 *
 * Examples:
 * - Optimistic locking failures
 * - Concurrent modification conflicts
 * - State transition conflicts
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictException(String errorCode, String message, Object... parameters) {
        super(errorCode, message, parameters);
    }
}
