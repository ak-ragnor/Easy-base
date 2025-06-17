package com.easyBase.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * Business logic exception
 *
 * Thrown when business rules are violated or business operations fail.
 * Results in HTTP 400 Bad Request responses.
 *
 * Examples:
 * - Email already exists
 * - Cannot delete last administrator
 * - Invalid business state transitions
 * - Validation failures beyond simple field validation
 *
 * @author Enterprise Team
 * @version 1.0
 * @since 1.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String errorCode, String message, Object... parameters) {
        super(errorCode, message, parameters);
    }

    public BusinessException(String errorCode, String message, Throwable cause, Object... parameters) {
        super(errorCode, message, cause, parameters);
    }
}
