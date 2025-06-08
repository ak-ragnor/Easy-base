package com.easyBase.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Resource not found exception
 *
 * Thrown when a requested resource cannot be found.
 * Results in HTTP 404 Not Found responses.
 *
 * Examples:
 * - User not found by ID
 * - User not found by email
 * - Related entity not found
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String errorCode, String message, Object... parameters) {
        super(errorCode, message, parameters);
    }

    public ResourceNotFoundException(String errorCode, String message, Throwable cause, Object... parameters) {
        super(errorCode, message, cause, parameters);
    }
}
