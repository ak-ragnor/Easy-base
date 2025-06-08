package com.easyBase.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Access denied exception
 *
 * Thrown when a user doesn't have permission to perform an operation.
 * Results in HTTP 403 Forbidden responses.
 *
 * Examples:
 * - User trying to access admin-only features
 * - User trying to modify other users they can't manage
 * - Role-based access violations
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessDeniedException(String errorCode, String message, Object... parameters) {
        super(errorCode, message, parameters);
    }
}