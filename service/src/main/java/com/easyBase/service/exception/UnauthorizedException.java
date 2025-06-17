package com.easyBase.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * unauthorized exception
 *
 * Thrown when a user is not authorized.
 * Results in HTTP 403 Forbidden responses.
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(String errorCode, String message, Object... parameters) {
        super(errorCode, message, parameters);
    }
}